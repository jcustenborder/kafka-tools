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

import com.github.jcustenborder.kafka.tools.CommonArguments;
import com.github.jcustenborder.kafka.tools.Tool;
import com.github.jcustenborder.kafka.tools.ToolRunner;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static net.sourceforge.argparse4j.impl.Arguments.store;

public class WaitForISRTool implements Tool {
  private static final Logger log = LoggerFactory.getLogger(WaitForISRTool.class);

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
        .dest("maxWaitTimeUnit")
        .setDefault(TimeUnit.MINUTES);

    final Argument maxWaitTime = parser.addArgument("--max-wait-time")
        .action(store())
        .help("The amount of time to wait.")
        .type(Long.class)
        .setDefault(10L)
        .dest("maxWaitTime");

    final Argument timeBetweenChecksUnit = parser.addArgument("--time-between-checks-unit")
        .action(store())
        .help("The unit of time to wait.")
        .type(TimeUnit.class)
        .dest("timeBetweenChecksUnit")
        .setDefault(TimeUnit.SECONDS);

    final Argument timeBetweenChecks = parser.addArgument("--time-between-checks")
        .action(store())
        .help("The amount of time to wait.")
        .type(Long.class)
        .setDefault(30L)
        .dest("timeBetweenChecks");

    final Argument brokerArgument = parser.addArgument("broker")
        .action(store())
        .dest("brokers")
        .help("The broker id(s) that the tool should check for.")
        .type(Integer.class)
        .nargs("+")
        .required(true);
//
//    return namespace -> {
//      ImmutableWaitForISRToolOptions.Builder builder = ImmutableWaitForISRToolOptions.builder();
//      builder.bootstrapServer(CommonArguments.bootstrapServer(namespace));
//      builder.maxWaitTimeUnit(namespace.get(maxWaitTimeUnit.getDest()));
//      builder.maxWaitTime(namespace.getLong(maxWaitTime.getDest()));
//      builder.timeBetweenChecksUnit(namespace.get(timeBetweenChecksUnit.getDest()));
//      builder.timeBetweenChecks(namespace.getLong(timeBetweenChecks.getDest()));
//      builder.outputFile(CommonArguments.outputFile(namespace));
//      builder.addAllBrokerIds(namespace.getList(brokerArgument.getDest()));
//      return builder.build();
//    };
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
//    log.debug("{}", option);
//    try (AdminClient adminClient = KafkaAdminClient.create(
//        ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, option.bootstrapServer())
//    )) {
//      AdminClientHelper adminClientHelper = new AdminClientHelper(adminClient);
//
//      Stopwatch stopwatch = Stopwatch.createStarted();
//
//      final long maxElapsedTime = TimeUnit.MILLISECONDS.convert(option.maxWaitTime(), option.maxWaitTimeUnit());
//
//      Multimap<Integer, TopicPartition> underReplicatedPartitions;
//      int underReplicatedPartitionCount;
//      while (true) {
//        underReplicatedPartitions = adminClientHelper.findUnderReplicatedPartitions(60, TimeUnit.SECONDS);
//        underReplicatedPartitionCount = 0;
//        for (Integer brokerId : option.brokerIds()) {
//          Collection<TopicPartition> topicPartitions = underReplicatedPartitions.get(brokerId);
//          int countForBroker = topicPartitions.size();
//          underReplicatedPartitionCount += countForBroker;
//          log.trace("Found {} under replicated partition(s) for broker {}.", underReplicatedPartitionCount, brokerId);
//        }
//
//        if (underReplicatedPartitionCount == 0) {
//          log.info("No under replicated partitions.");
//          break;
//        }
//        log.warn("Found {} under replicated partition(s) across broker(s) {}.", underReplicatedPartitionCount, option.brokerIds());
//        final long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
//        if (elapsed > maxElapsedTime) {
//          log.error("{} under replicated partition(s) found.", underReplicatedPartitionCount);
//          break;
//        }
//        long timeUntilAbort = maxElapsedTime - elapsed;
//        long abortTimeSeconds = TimeUnit.SECONDS.convert(timeUntilAbort, TimeUnit.MILLISECONDS);
//        long sleepIntervalSeconds = TimeUnit.SECONDS.convert(option.timeBetweenChecks(), option.timeBetweenChecksUnit());
//        log.info("Waiting {} seconds for next check. Aborting in {} second(s).", sleepIntervalSeconds, abortTimeSeconds);
//        long sleepInterval = TimeUnit.MILLISECONDS.convert(option.timeBetweenChecks(), option.timeBetweenChecksUnit());
//        Thread.sleep(sleepInterval);
//      }
//
//      WaitForISRState state = ImmutableWaitForISRState.builder()
//          .addAllBrokerIDs(option.brokerIds())
//          .underReplicatedPartitions(underReplicatedPartitionCount)
//          .putAllUnderReplicatedPartitionsByBroker(underReplicatedPartitions)
//          .hasUnderReplicatedPartitions(underReplicatedPartitionCount > 0)
//          .build();
//      if (null != option.outputFile()) {
//        ObjectMapperFactory.write(option.outputFile(), state);
//      } else {
//        log.info("{}", state);
//      }
//    }
  }


  @Override
  public void close() throws Exception {

  }
}
