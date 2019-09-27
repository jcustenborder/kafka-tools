package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.KafkaConnectException;
import com.github.jcustenborder.kafka.connect.client.model.CreateOrUpdateConnectorResponse;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImportTool extends AbstractConnectorTool {
  private static final Logger log = LoggerFactory.getLogger(ImportTool.class);

  @Override
  public String name() {
    return "import";
  }

  @Override
  public String description() {
    return "Command is used to configure the connection information for the connect cluster.";
  }

  @Override
  public void options(ArgumentParser parser) {
    addInputPathArgument(parser)
        .required(true);

  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    File inputPath = inputPath(namespace);
    ConfigHelper configHelper = configHelper(namespace);
    List<File> configurationFiles = Arrays.stream(inputPath.listFiles())
        .filter(ConfigHelper::hasHandler)
        .collect(Collectors.toList());
    log.info("Found {} configuration(s). Validating configuration(s)", configurationFiles.size());
    KafkaConnectClient client = client(namespace);

    final AtomicInteger errors = new AtomicInteger(0);
    final ListMultimap<String, File> connectorToFile = LinkedListMultimap.create();
    final Map<String, Map<String, String>> connectors = new LinkedHashMap<>();

    configurationFiles.stream()
        .sorted(File::compareTo)
        .forEach(configFile -> {
          try {
            final Map<String, String> config = configHelper.load(configFile);
            final String connectorName = configHelper.connectorName(config);
            final String connectorClass = configHelper.connectorClass(config);
            if (Strings.isNullOrEmpty(connectorName)) {
              log.error("{} missing connector name.", configFile);
              return;
            }
            if (Strings.isNullOrEmpty(connectorClass)) {
              log.error("{} missing connector class.", configFile);
              return;
            }
            connectorToFile.put(connectorName, configFile);
            List<File> otherFiles = connectorToFile.get(connectorName);
            if (otherFiles.size() > 1) {
              log.error(
                  "Connector {} is defined in more than one file. {}",
                  connectorName,
                  Joiner.on(", ").join(otherFiles)
              );
              errors.incrementAndGet();
            }
            try {
              log.info("Validating configuration for '{}' against REST Service.", connectorName);
              ValidateResponse validateResponse = client.validate(
                  connectorClass,
                  config
              );
              if (validateResponse.errorCount() > 0) {
                log.error("Connector validation returned {} error(s).", validateResponse.errorCount());
                errors.addAndGet(validateResponse.errorCount());

                validateResponse.configs().stream()
                    .filter(configElement -> null != configElement.value().errors() && !configElement.value().errors().isEmpty())
                    .flatMap(new Function<ValidateResponse.ConfigElement, Stream<String>>() {
                      @Nullable
                      @Override
                      public Stream<String> apply(@Nullable ValidateResponse.ConfigElement configElement) {
                        return configElement.value().errors().stream();
                      }
                    })
                    .forEach(error -> log.error("Error(s) found in {}: {}", configFile, error));
              } else {
                log.debug("Valid configuration for '{}'", connectorName);
              }
            } catch (IOException ex) {
              log.error(
                  "Exception while validating connector '{}' of {},",
                  connectorName,
                  configFile,
                  ex
              );
              errors.incrementAndGet();
            }

            log.info("Loaded '{}' from {}", connectorName, configFile);
            connectors.put(connectorName, config);
          } catch (IOException e) {
            log.error("Exception thrown while reading {}.", configFile, e);
            errors.incrementAndGet();
          }
        });
    Preconditions.checkState(
        errors.get() == 0,
        "%s errors were encountered. No configurations were uploaded.",
        errors.get()
    );


    for (Map.Entry<String, Map<String, String>> entry : connectors.entrySet()) {
      final String connectorName = entry.getKey();
      final Map<String, String> config = entry.getValue();
      Map<String, String> existingConfig;

      try {
        log.info("Downloading existing config for {}'", connectorName);
        existingConfig = client.config(connectorName);
      } catch (KafkaConnectException ex) {
        if (404 == ex.errorCode()) {
          log.info("Connector {} does not exist on the cluster.", connectorName);
          existingConfig = ImmutableMap.of();
        } else {
          log.error("Exception thrown", ex);
          continue;
        }
      }

      log.info("Existing config for {}: {}", connectorName, existingConfig);
      MapDifference<String, String> differences = Maps.difference(existingConfig, config);

      if (differences.areEqual()) {
        log.info("Config for {} is the same", connectorName);
        continue;
      }

      try {
        log.info("Updating configuration for '{}'", connectorName);
        CreateOrUpdateConnectorResponse response = client.createOrUpdate(connectorName, config);
      } catch (IOException ex) {
        log.error("Exception thrown while updating config for '{}'", connectorName, ex);
      }

    }

  }


}
