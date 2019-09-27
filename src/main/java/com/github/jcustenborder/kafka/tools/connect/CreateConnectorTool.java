package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.tools.Tool;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateConnectorTool extends AbstractConnectorTool {
  private static final Logger log = LoggerFactory.getLogger(CreateConnectorTool.class);

  @Override
  public String name() {
    return "create";
  }

  @Override
  public String description() {
    return "Command is used to create or update a connector on the Kafka Connect Cluster.";
  }

  @Override
  public void options(ArgumentParser parser) {

  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    log.info("Creating something.");
  }
}
