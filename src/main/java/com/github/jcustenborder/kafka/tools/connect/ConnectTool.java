package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.tools.MultiCommandTool;
import com.github.jcustenborder.kafka.tools.ObjectMapperFactory;
import com.github.jcustenborder.kafka.tools.ConsoleFormat;
import com.github.jcustenborder.kafka.tools.Tool;
import com.github.jcustenborder.kafka.tools.ToolRunner;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static net.sourceforge.argparse4j.impl.Arguments.store;

public class ConnectTool extends MultiCommandTool {
  private static final Logger log = LoggerFactory.getLogger(ConnectTool.class);

  public static void main(String... args) throws Exception {
    ToolRunner.run(new ConnectTool(), args);
  }

  @Override
  public String name() {
    return "connect-client";
  }

  @Override
  public String description() {
    return "The " + name() + " command provides additinal functionality to assist with the " +
        "administration of a Kafka Connect cluster. All actions are performed over the Kafka " +
        "Connect REST API.";
  }

  @Override
  protected List<Tool> tools() {
    return Arrays.asList(
        new CreateConnectorTool(),
        new RestartConnectorTool(),
        new RestartTaskTool(),
        new ConfigureTool(),
        new ImportTool(),
        new ExportTool(),
        new ListTool(),
        new ConnectorPluginsTool(),
        new DeleteTool(),
        new DownloadExampleTool(),
        new StatusTool()
    );
  }

  @Override
  public void options(ArgumentParser parser) {
    final String homeDirectory = System.getProperty("user.home");
    File connectOptionsFile = new File(
        homeDirectory,
        ".connect.json"
    );
    Argument connectConfigArgument = parser.addArgument("--connect-config")
        .action(store())
        .help("Location of the connect config file.")
        .dest(ConnectConstants.DEST_CONNECT_CONFIG)
        .setDefault(connectOptionsFile)
        .type(File.class)
        .required(false);

    String defaultHost;
    Integer defaultPort;
    if (connectOptionsFile.isFile()) {
      try {
        ConnectConfig config = ObjectMapperFactory.INSTANCE.readValue(connectOptionsFile, ConnectConfig.class);
        defaultHost = config.host();
        defaultPort = config.port();
      } catch (IOException e) {
        log.warn("Could not load {}", connectOptionsFile, e);
        defaultHost = null;
        defaultPort = null;
      }
    } else {
      defaultHost = null;
      defaultPort = null;
    }

    Argument hostArgument = parser.addArgument("--host")
        .action(store())
        .help("Kafka Connect host name.")
        .dest(ConnectConstants.DEST_HOST)
        .required(false)
        .setDefault(null == defaultHost ? "localhost" : defaultHost)
        .type(String.class);
    Argument portArgument = parser.addArgument("--port")
        .action(store())
        .help("Port on the Kafka Connect host to connect to.")
        .dest(ConnectConstants.DEST_PORT)
        .required(false)
        .setDefault(null == defaultPort ? 8083 : defaultPort)
        .type(Integer.class);
    Argument outputFormatArgument = parser.addArgument("--output-format")
        .action(store())
        .dest(ConnectConstants.DEST_CONSOLE_OUTPUT_FORMAT)
        .help("")
        .required(false)
        .type(ConsoleFormat.class)
        .setDefault(ConsoleFormat.Table);

    super.options(parser);

  }

  @Override
  public void execute(Namespace namespace) throws Exception {

  }

  @Override
  public void close() throws Exception {

  }
}