package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatusResponse;
import com.github.jcustenborder.kafka.tools.Table;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class StatusTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(StatusTool.class);

  @Override
  public String name() {
    return "status";
  }

  @Override
  public String description() {
    return "Command is used to return the status of a connector and it's tasks.";
  }


  @Override
  public void arguments(ArgumentParser parser) {
    addConnectorArgument(parser)
        .required(true);
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    KafkaConnectClient client = client(namespace);
    List<String> connectors = namespace.getList(ConnectConstants.DEST_CONNECTOR);


    connectors.stream()
        .sorted()
        .forEach(connector -> {
          Table table = table(namespace);
          try {
            ConnectorStatusResponse status = client.status(connector);
            table.addRow(
                "type", "connector",
                "name", connector,
                "status", status.connector().state(),
                "worker", status.connector().workerID(),
                "trace", status.connector().trace()
            );
            status.tasks().forEach(task ->
                table.addRow(
                    "type", "connector",
                    "name", task.id(),
                    "status", task.state(),
                    "worker", task.workerID(),
                    "trace", task.trace()
                )
            );
          } catch (IOException e) {
            table.addRow(
                "name", connector,
                "status", e.getMessage()
            );
          }
          log.info("\n{}", table);
        });

  }
}
