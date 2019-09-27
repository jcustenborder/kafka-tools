package com.github.jcustenborder.kafka.tools.connect;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableConnectConfig.class)
public interface ConnectConfig {
  String host();

  int port();
}
