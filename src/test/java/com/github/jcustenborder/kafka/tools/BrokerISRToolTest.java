/**
 * Copyright © 2019 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.tools;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Disabled
public class BrokerISRToolTest {
  private static final Logger log = LoggerFactory.getLogger(BrokerISRToolTest.class);

  @Test
  public void findUnderReplicatedForBrokerID() throws Exception {
    AdminClient adminClient = AdminClient.create(
        ImmutableMap.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092",
            AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, Long.toString(TimeUnit.MILLISECONDS.convert(120, TimeUnit.SECONDS))
        )
    );

    final int adminClientTimeout = (int) TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);
    ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
    listTopicsOptions.timeoutMs(adminClientTimeout);
    listTopicsOptions.listInternal(true);

    ListTopicsResult listTopicsResult = adminClient.listTopics(listTopicsOptions);
    Set<String> topicNameSet = listTopicsResult.names().get(60, TimeUnit.SECONDS);
    List<String> topicNames = new ArrayList<>(topicNameSet);

    final Multimap<Integer, TopicPartition> underReplicatedPartitions = MultimapBuilder.linkedHashKeys().arrayListValues().build();

    for (List<String> topicChunk : Lists.partition(topicNames, 10)) {
      DescribeTopicsOptions options = new DescribeTopicsOptions();
      DescribeTopicsResult result = adminClient.describeTopics(topicChunk, options);
      Map<String, TopicDescription> topicDescriptions = result.all().get(60, TimeUnit.SECONDS);
      for (TopicDescription topicDescription : topicDescriptions.values()) {
        for (TopicPartitionInfo topicPartitionInfo : topicDescription.partitions()) {
          TopicPartition topicPartition = new TopicPartition(topicDescription.name(), topicPartitionInfo.partition());
          for (Node replicaNode : topicPartitionInfo.replicas()) {
            Optional<Node> optionalISRNode = topicPartitionInfo.isr()
                .stream()
                .filter(isrNode -> replicaNode.id() == isrNode.id())
                .findFirst();
            if (!optionalISRNode.isPresent()) {
              log.warn(
                  "Broker {} is a replica for {} but is not an ISR.",
                  replicaNode.id(),
                  topicPartition
              );
              underReplicatedPartitions.put(
                  replicaNode.id(),
                  topicPartition
              );
            } else {
              log.trace(
                  "Broker {} is a replica for {} but and is an ISR.",
                  replicaNode.id(),
                  topicPartition
              );
            }
          }
        }
      }
    }

    Collection<TopicPartition> brokerUnderReplicatedPartitions = underReplicatedPartitions.get(1);
    log.info("Found {} under replicated partitions for broker {}", brokerUnderReplicatedPartitions.size(), 1);
  }

}
