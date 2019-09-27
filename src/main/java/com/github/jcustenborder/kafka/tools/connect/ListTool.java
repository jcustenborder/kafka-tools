package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.tools.Table;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;


public class ListTool extends AbstractConnectorTool {
  private static final Logger log = LoggerFactory.getLogger(ListTool.class);

  @Override
  public String name() {
    return "list";
  }

  @Override
  public String description() {
    return "Command is used to list the connectors on the connect cluster.";
  }

  @Override
  public void options(ArgumentParser parser) {


  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    KafkaConnectClient client = client(namespace);
    List<String> connectors = client.connectors();
    Table table = table(namespace);
    connectors.stream()
        .sorted()
        .forEach(s -> table.addRow("name", s));
    log.info("{}\n", table);
  }
}
