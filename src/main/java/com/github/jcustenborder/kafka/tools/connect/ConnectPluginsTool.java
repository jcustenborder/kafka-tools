package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorPlugin;
import com.github.jcustenborder.kafka.tools.Table;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ConnectPluginsTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(ConnectPluginsTool.class);

  @Override
  public String name() {
    return "connector-plugins";
  }

  @Override
  public String description() {
    return "Command is used to list all of the connector plugins that are registered on the " +
        "Kafka Connect Worker.";
  }

  @Override
  public void options(ArgumentParser parser) {


  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    KafkaConnectClient client = client(namespace);
    List<ConnectorPlugin> connectors = client.connectorPlugins();
    Table table = table(namespace);

    for (ConnectorPlugin plugin : connectors) {
      table.addRow(
          "className",
          plugin.className(),
          "version",
          plugin.version()
      );
    }
    log.info("\n{}", table);
  }
}
