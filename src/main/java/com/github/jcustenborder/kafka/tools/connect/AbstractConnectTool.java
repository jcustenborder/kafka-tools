package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.KafkaConnectClientFactory;
import com.github.jcustenborder.kafka.tools.ConsoleFormat;
import com.github.jcustenborder.kafka.tools.Table;
import com.github.jcustenborder.kafka.tools.Tool;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.sourceforge.argparse4j.impl.Arguments.store;

public abstract class AbstractConnectTool implements Tool {


  protected ExecutorService executorService;

  public AbstractConnectTool() {
    executorService = Executors.newScheduledThreadPool(2);
  }

  protected Argument addConnectorArgument(ArgumentParser parser) {
    return parser.addArgument("--connector")
        .action(store())
        .nargs("+")
        .dest(ConnectConstants.DEST_CONNECTOR)
        .type(String.class)
        .help("The name of the connector");
  }

  protected Argument addOutputFileArgument(ArgumentParser parser) {
    return parser.addArgument("--output-file")
        .action(store())
        .dest(ConnectConstants.DEST_OUTPUT_FILE)
        .type(File.class)
        .help("The path on the file system to write the output to.");
  }

  protected Argument addOutputPathArgument(ArgumentParser parser) {
    return parser.addArgument("--output-path")
        .action(store())
        .dest(ConnectConstants.DEST_OUTPUT_PATH)
        .type(File.class)
        .help("Directory on the file system to write the output to.");
  }

  protected Argument addInputPathArgument(ArgumentParser parser) {
    return parser.addArgument("--input-path")
        .action(store())
        .dest(ConnectConstants.DEST_INPUT_PATH)
        .type(File.class)
        .help("Directory to read connector configuration(s) from.");
  }

  protected Argument addConfigPathArgument(ArgumentParser parser) {
    return parser.addArgument("--config-path")
        .action(store())
        .dest(ConnectConstants.DEST_CONFIG_PATH)
        .type(File.class)
        .help("Location on the local file system to read the config from.");
  }


  protected Argument addOutputFormatArgument(ArgumentParser parser) {
    return parser.addArgument("--output-format")
        .action(store())
        .dest(ConnectConstants.DEST_OUTPUT_FORMAT)
        .type(ConfigHelper.OutputFormat.class)
        .required(false)
        .setDefault(ConfigHelper.OutputFormat.Json)
        .help("The output format used to write configuration(s) to the file system.");
  }

  protected String connector(Namespace namespace) {
    return namespace.getString(ConnectConstants.DEST_CONNECTOR);
  }

  protected File outputFile(Namespace namespace) {
    return namespace.get(ConnectConstants.DEST_OUTPUT_FILE);
  }

  protected File outputPath(Namespace namespace) {
    return namespace.get(ConnectConstants.DEST_OUTPUT_PATH);
  }

  protected File inputPath(Namespace namespace) {
    return namespace.get(ConnectConstants.DEST_INPUT_PATH);
  }

  protected File configPath(Namespace namespace) {
    return namespace.get(ConnectConstants.DEST_CONFIG_PATH);
  }

  protected ConfigHelper.OutputFormat outputFormat(Namespace namespace) {
    return namespace.get(ConnectConstants.DEST_OUTPUT_FORMAT);
  }


  protected KafkaConnectClient client(Namespace namespace) {
    String host = namespace.getString(ConnectConstants.DEST_HOST);
    Integer port = namespace.getInt(ConnectConstants.DEST_PORT);

    KafkaConnectClientFactory factory = new KafkaConnectClientFactory();
    factory.executorService(executorService);
    factory.host(host);
    factory.port(port);
    return factory.createClient();
  }

  protected Table table(Namespace namespace) {
    ConsoleFormat consoleFormat = namespace.get(ConnectConstants.DEST_CONSOLE_OUTPUT_FORMAT);
    Table table = new Table();
    table.outputFormat(consoleFormat);
    return table;
  }


  protected ConfigHelper configHelper(Namespace namespace) {
    ConfigHelper.OutputFormat outputFormat = outputFormat(namespace);
    if (null == outputFormat) {
      outputFormat = ConfigHelper.OutputFormat.Json;
    }
    ConfigHelper configHelper = new ConfigHelper(outputFormat);
    return configHelper;
  }

  @Override
  public void close() throws Exception {
    this.executorService.shutdown();
  }
}
