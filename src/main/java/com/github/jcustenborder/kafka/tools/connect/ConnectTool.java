package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.tools.ConsoleFormat;
import com.github.jcustenborder.kafka.tools.MultiCommandTool;
import com.github.jcustenborder.kafka.tools.ObjectMapperFactory;
import com.github.jcustenborder.kafka.tools.Tool;
import com.github.jcustenborder.kafka.tools.ToolRunner;
import com.google.common.base.Strings;
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
        new CreateConnectTool(),
        new RestartConnectTool(),
        new RestartTaskTool(),
        new ConfigureTool(),
        new ImportTool(),
        new ExportTool(),
        new ListTool(),
        new ConnectPluginsTool(),
        new DeleteTool(),
        new DownloadExampleTool(),
        new StatusTool(),
        new PauseTool(),
        new ResumeTool()
    );
  }

  @Override
  public void arguments(ArgumentParser parser) {
    final String homeDirectory = System.getProperty("user.home");
    File connectOptionsFile = new File(
        homeDirectory,
        ".connect.json"
    );
    Argument connectConfigArgument = parser.addArgument("--connect-config")
        .action(store())
        .help("Location of the config file this utility will use to store connection information" +
            " about the Kafka Connect Cluster.")
        .dest(ConnectConstants.DEST_CONNECT_CONFIG)
        .setDefault(connectOptionsFile)
        .type(File.class)
        .required(false);

    String defaultHost;
    Integer defaultPort;
    String username;
    String password;
    String scheme;
    if (connectOptionsFile.isFile()) {
      try {
        ConnectConfig config = ObjectMapperFactory.INSTANCE.readValue(connectOptionsFile, ConnectConfig.class);
        defaultHost = config.host();
        defaultPort = config.port();
        username = config.username();
        password = config.password();
        scheme = config.scheme();
      } catch (IOException e) {
        log.warn("Could not load {}", connectOptionsFile, e);
        defaultHost = null;
        defaultPort = null;
        username = null;
        password = null;
        scheme = null;
      }
    } else {
      defaultHost = null;
      defaultPort = null;
      username = null;
      password = null;
      scheme = null;
    }

    Argument hostArgument = parser.addArgument("--host")
        .action(store())
        .help("Host of the Kafka Connect cluster to connect to.")
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
    Argument schemeArgument = parser.addArgument("--scheme")
        .action(store())
        .help("Scheme to connect to the Kafka Connect host with.")
        .dest(ConnectConstants.DEST_SCHEME)
        .required(false)
        .setDefault(Strings.isNullOrEmpty(scheme) ? "http" : scheme)
        .type(String.class);
    Argument usernameArgument = parser.addArgument("--username")
        .action(store())
        .help("Username to connect to the Kafka Connect host with.")
        .dest(ConnectConstants.DEST_USERNAME)
        .required(false)
        .setDefault(Strings.isNullOrEmpty(username) ? null : username)
        .type(String.class);
    Argument passwordArgument = parser.addArgument("--password")
        .action(store())
        .help("Password to connect to the Kafka Connect host with.")
        .dest(ConnectConstants.DEST_PASSWORD)
        .required(false)
        .setDefault(Strings.isNullOrEmpty(password) ? null : password)
        .type(String.class);

    Argument outputFormatArgument = parser.addArgument("--output-format")
        .action(store())
        .dest(ConnectConstants.DEST_CONSOLE_OUTPUT_FORMAT)
        .help("The format written to the console when information is displayed.")
        .required(false)
        .type(ConsoleFormat.class)
        .setDefault(ConsoleFormat.Table);

    super.arguments(parser);
  }

  @Override
  public void execute(Namespace namespace) throws Exception {

  }

  @Override
  public void close() throws Exception {

  }
}
