package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ExportTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(ExportTool.class);

  @Override
  public String name() {
    return "export";
  }

  @Override
  public String description() {
    return "Command is used to export the connector configurations from the Kafka Connect Cluster to" +
        "the local file system.";
  }

  @Override
  public void options(ArgumentParser parser) {
    addOutputPathArgument(parser)
        .required(true);
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    File outputPath = outputPath(namespace);
    ConfigHelper configHelper = configHelper(namespace);

    KafkaConnectClient client = client(namespace);
    List<String> connectors = client.connectors();

    Map<String, Map<String, String>> configs = new LinkedHashMap<>();

    final AtomicInteger errors = new AtomicInteger(0);
    connectors.stream()
        .sorted()
        .forEach(connector -> {
          log.info("Downloading connector configuration '{}'", connector);
          try {
            Map<String, String> config = client.config(connector);
            configs.put(connector, config);
          } catch (IOException e) {
            log.error("Exception thrown while downloading configuration for '{}'", connector, e);
            errors.incrementAndGet();
          }
        });

    if (errors.get() > 0) {
      log.error("Error(s) were encountered while downloading connector configs");
      return;
    }

    log.info("Writing {} connector configuration(s) to {}", configs.size(), outputPath);
    for (Map.Entry<String, Map<String, String>> config : configs.entrySet()) {
      File configPath = configHelper.fileName(outputPath, config.getKey());
      configHelper.save(config.getValue(), configPath);
    }
  }

}
