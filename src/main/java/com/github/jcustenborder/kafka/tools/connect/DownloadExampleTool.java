package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorPlugin;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static net.sourceforge.argparse4j.impl.Arguments.store;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;


public class DownloadExampleTool extends AbstractConnectorTool {
  private static final Logger log = LoggerFactory.getLogger(DownloadExampleTool.class);

  @Override
  public String name() {
    return "download-example";
  }

  @Override
  public String description() {
    return "Command is used to download configuration required to create a new connector.";
  }

  private static final String DEST_CLASS_NAME = "className";
  private static final String DEST_INCLUDE_DEFAULTS = "includeDefaults";

  @Override
  public void options(ArgumentParser parser) {
    addOutputFormatArgument(parser);
    addOutputFileArgument(parser)
        .required(true);
    parser.addArgument("--class")
        .action(store())
        .dest(DEST_CLASS_NAME)
        .help("Class name of the connector to download settings for.")
        .required(true);

    parser.addArgument("--include-defaults")
        .action(storeTrue())
        .dest(DEST_INCLUDE_DEFAULTS)
        .help("Flag to determine if the config items that have default values should be included with their defaults.")
        .required(false)
        .setDefault(false);
  }

  ConnectorPlugin findPlugin(List<ConnectorPlugin> plugins, String className) {
    ConnectorPlugin result;

    log.trace("findPlugin() - Searching for {}", className);
    Optional<ConnectorPlugin> optionalPlugin = plugins.stream()
        .filter(p -> p.className().equalsIgnoreCase(className))
        .findFirst();
    if (optionalPlugin.isPresent()) {
      result = optionalPlugin.get();
    } else {
      optionalPlugin = plugins.stream()
          .filter(p -> p.className().endsWith(className))
          .findFirst();
      result = optionalPlugin.orElse(null);
    }
    return result;
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    String className = namespace.getString(DEST_CLASS_NAME);
    boolean includeDefaults = namespace.getBoolean(DEST_INCLUDE_DEFAULTS);
    File outputFile = outputFile(namespace);

    KafkaConnectClient client = client(namespace);
    List<ConnectorPlugin> plugins = client.connectorPlugins();

    ConnectorPlugin plugin = findPlugin(plugins, className);

    if (null == plugin) {
      log.warn("Could not find plugin '{}'", className);
      return;
    }

    ConfigHelper configHelper = configHelper(namespace);

    Map<String, String> baseConfig = new LinkedHashMap<>();
    baseConfig.put("name", "example");
    baseConfig.put("connector.class", plugin.className());
    baseConfig.put("tasks.max", "1");
    baseConfig.put("topics", "foo");

    ValidateResponse validateResponse = client.validate(plugin.className(), baseConfig);
    Map<String, String> exampleConfig = new LinkedHashMap<>(baseConfig);


    Predicate<ValidateResponse.ConfigElement> filter;

    if (includeDefaults) {
      filter = configElement -> true;
    } else {
      filter = configElement -> configElement.definition().required();
    }

    validateResponse.configs().stream()
        .filter(filter)
        .forEach(c -> {
          exampleConfig.put(c.value().name(), c.value().value());
        });

    configHelper.save(exampleConfig, outputFile);
  }
}
