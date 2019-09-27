package com.github.jcustenborder.kafka.tools.connect;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    log.info("Creating something.");
  }
}
