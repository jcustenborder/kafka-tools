package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.tools.Table;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class RestartTaskTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(RestartTaskTool.class);

  @Override
  public String name() {
    return "restart-task";
  }

  @Override
  public String description() {
    return "Command is used to restart a task for a connector.";
  }

  @Override
  public void options(ArgumentParser parser) {
    addConnectorArgument(parser);
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    KafkaConnectClient client = client(namespace);
    List<String> connectors = namespace.getList(ConnectConstants.DEST_CONNECTOR);
    Table table = table(namespace);
    connectors.stream()
        .sorted()
        .forEach(connector -> {
          try {
            client.restart(connector);
            table.addRow(
                "name", connector,
                "status", "restarted"
            );
          } catch (IOException e) {
            table.addRow(
                "name", connector,
                "status", e.getMessage()
            );
          }
        });
    log.info("\n{}", table);
  }

}
