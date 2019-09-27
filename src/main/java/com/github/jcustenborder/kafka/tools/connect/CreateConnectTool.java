package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.CreateOrUpdateConnectorResponse;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class CreateConnectTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(CreateConnectTool.class);

  @Override
  public String name() {
    return "create";
  }

  @Override
  public String description() {
    return "Command is used to create or update a connector on the Kafka Connect Cluster.";
  }

  @Override
  public void arguments(ArgumentParser parser) {
    addConfigPathArgument(parser)
        .required(true);
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    final File configPath = configPath(namespace);
    ConfigHelper configHelper = configHelper(namespace);
    Map<String, String> config = configHelper.load(configPath);
    String connectorName = configHelper.connectorName(config);
    Preconditions.checkState(
        !Strings.isNullOrEmpty(connectorName),
        "name must be specified in the configuration"
    );
    KafkaConnectClient client = client(namespace);
    CreateOrUpdateConnectorResponse response = client.createOrUpdate(connectorName, config);
    log.info("Created {}", response.name());
  }
}
