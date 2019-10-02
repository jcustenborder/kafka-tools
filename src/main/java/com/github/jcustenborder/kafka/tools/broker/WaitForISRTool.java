/**
 * Copyright © 2019 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.tools.broker;

import com.github.jcustenborder.kafka.tools.AdminClientHelper;
import com.github.jcustenborder.kafka.tools.CommonArguments;
import com.github.jcustenborder.kafka.tools.ObjectMapperFactory;
import com.github.jcustenborder.kafka.tools.Tool;
import com.github.jcustenborder.kafka.tools.ToolRunner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.sourceforge.argparse4j.impl.Arguments.store;

public class WaitForISRTool implements Tool {
  private static final Logger log = LoggerFactory.getLogger(WaitForISRTool.class);
  static final String DEST_MAX_WAIT_TIME_UNIT = "maxWaitTimeUnit";
  static final String DEST_MAX_WAIT_TIME = "maxWaitTime";
  static final String DEST_TIME_BETWEEN_CHECKS_UNIT = "timeBetweenChecksUnit";
  static final String DEST_TIME_BETWEEN_CHECKS = "timeBetweenChecks";
  static final String DEST_BROKERS = "brokers";

  public static void main(String... args) throws Exception {
    ToolRunner.run(new WaitForISRTool(), args);
  }

  @Override
  public String name() {
    return "wait-for-isr";
  }

  @Override
  public String description() {
    return "This tool is used to check if a broker(s) has under replicated partitions.";
  }

  @Override
  public void arguments(ArgumentParser parser) {
    final Argument bootstrapServerArgument = CommonArguments.bootstrapServer(parser);
    final Argument outputFileArgument = CommonArguments.outputFile(parser, false);


    final Argument maxWaitTimeUnit = parser.addArgument("--max-wait-time-unit")
        .action(store())
        .help("The unit of time to wait.")
        .type(TimeUnit.class)
        .dest(DEST_MAX_WAIT_TIME_UNIT)
        .setDefault(TimeUnit.MINUTES);

    final Argument maxWaitTime = parser.addArgument("--max-wait-time")
        .action(store())
        .help("The amount of time to wait.")
        .type(Long.class)
        .setDefault(10L)
        .dest(DEST_MAX_WAIT_TIME);

    final Argument timeBetweenChecksUnit = parser.addArgument("--time-between-checks-unit")
        .action(store())
        .help("The unit of time to wait.")
        .type(TimeUnit.class)
        .dest(DEST_TIME_BETWEEN_CHECKS_UNIT)
        .setDefault(TimeUnit.SECONDS);

    final Argument timeBetweenChecks = parser.addArgument("--time-between-checks")
        .action(store())
        .help("The amount of time to wait.")
        .type(Long.class)
        .setDefault(30L)
        .dest(DEST_TIME_BETWEEN_CHECKS);

    final Argument brokerArgument = parser.addArgument("broker")
        .action(store())
        .dest(DEST_BROKERS)
        .help("The broker id(s) that the tool should check for.")
        .type(Integer.class)
        .nargs("+")
        .required(true);
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    final String bootstrapServer = CommonArguments.bootstrapServer(namespace);
    final TimeUnit maxWaitTimeUnit = namespace.get(DEST_MAX_WAIT_TIME_UNIT);
    final long maxWaitTime = namespace.get(DEST_MAX_WAIT_TIME);
    final long timeBetweenChecks = namespace.get(DEST_TIME_BETWEEN_CHECKS);
    final TimeUnit timeBetweenChecksUnit = namespace.get(DEST_TIME_BETWEEN_CHECKS_UNIT);
    final List<Integer> brokerIds = namespace.get(DEST_BROKERS);
    final File outputFile = CommonArguments.outputFile(namespace);


    try (AdminClient adminClient = KafkaAdminClient.create(
        ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    )) {
      AdminClientHelper adminClientHelper = new AdminClientHelper(adminClient);

      Stopwatch stopwatch = Stopwatch.createStarted();

      final long maxElapsedTime = TimeUnit.MILLISECONDS.convert(maxWaitTime, maxWaitTimeUnit);

      Multimap<Integer, TopicPartition> underReplicatedPartitions;
      int underReplicatedPartitionCount;
      while (true) {
        underReplicatedPartitions = adminClientHelper.findUnderReplicatedPartitions(60, TimeUnit.SECONDS);
        underReplicatedPartitionCount = 0;
        for (Integer brokerId : brokerIds) {
          Collection<TopicPartition> topicPartitions = underReplicatedPartitions.get(brokerId);
          int countForBroker = topicPartitions.size();
          underReplicatedPartitionCount += countForBroker;
          log.trace("Found {} under replicated partition(s) for broker {}.", underReplicatedPartitionCount, brokerId);
        }

        if (underReplicatedPartitionCount == 0) {
          log.info("No under replicated partitions.");
          break;
        }
        log.warn("Found {} under replicated partition(s) across broker(s) {}.", underReplicatedPartitionCount, brokerIds);
        final long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (elapsed > maxElapsedTime) {
          log.error("{} under replicated partition(s) found.", underReplicatedPartitionCount);
          break;
        }
        long timeUntilAbort = maxElapsedTime - elapsed;
        long abortTimeSeconds = TimeUnit.SECONDS.convert(timeUntilAbort, TimeUnit.MILLISECONDS);
        long sleepIntervalSeconds = TimeUnit.SECONDS.convert(timeBetweenChecks, timeBetweenChecksUnit);
        log.info("Waiting {} seconds for next check. Aborting in {} second(s).", sleepIntervalSeconds, abortTimeSeconds);
        long sleepInterval = TimeUnit.MILLISECONDS.convert(timeBetweenChecks, timeBetweenChecksUnit);
        Thread.sleep(sleepInterval);
      }

      WaitForISRState state = ImmutableWaitForISRState.builder()
          .addAllBrokerIDs(brokerIds)
          .underReplicatedPartitions(underReplicatedPartitionCount)
          .putAllUnderReplicatedPartitionsByBroker(underReplicatedPartitions)
          .hasUnderReplicatedPartitions(underReplicatedPartitionCount > 0)
          .build();
      if (null != outputFile) {
        ObjectMapperFactory.write(outputFile, state);
      } else {
        log.info("{}", state);
      }
    }
  }


  @Override
  public void close() throws Exception {

  }
}
