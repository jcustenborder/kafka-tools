package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.tools.ObjectMapperFactory;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ConfigureTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(ConfigureTool.class);

  @Override
  public String name() {
    return "configure";
  }

  @Override
  public String description() {
    return "Command is used to configure the connection information for the connect cluster.";
  }

  @Override
  public void arguments(ArgumentParser parser) {

  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    ConnectConfig config = ImmutableConnectConfig.builder()
        .host(namespace.getString(ConnectConstants.DEST_HOST))
        .port(namespace.getInt(ConnectConstants.DEST_PORT))
        .username(namespace.get(ConnectConstants.DEST_USERNAME))
        .password(namespace.get(ConnectConstants.DEST_PASSWORD))
        .scheme(namespace.get(ConnectConstants.DEST_SCHEME))
        .build();

    File connectConfigFile = namespace.get(ConnectConstants.DEST_CONNECT_CONFIG);
    log.info("Writing config to {}", connectConfigFile);
    ObjectMapperFactory.INSTANCE.writeValue(connectConfigFile, config);
  }
}
