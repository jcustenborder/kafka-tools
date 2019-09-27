package com.github.jcustenborder.kafka.tools.broker;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Multimap;
import org.apache.kafka.common.TopicPartition;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize
public interface WaitForISRState {
  List<Integer> brokerIDs();

  Multimap<Integer, TopicPartition> underReplicatedPartitionsByBroker();

  int underReplicatedPartitions();

  boolean hasUnderReplicatedPartitions();
}
