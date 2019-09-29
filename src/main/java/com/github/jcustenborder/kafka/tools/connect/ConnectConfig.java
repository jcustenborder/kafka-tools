package com.github.jcustenborder.kafka.tools.connect;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonDeserialize(as = ImmutableConnectConfig.class)
public interface ConnectConfig {
  String host();

  int port();

  @Nullable
  String username();

  @Nullable
  String password();

  @Nullable
  String scheme();
}
