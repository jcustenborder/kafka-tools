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

import com.google.common.collect.Multimap;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.KafkaFutureImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled
public class AdminClientHelperTest {
  AdminClient adminClient;
  AdminClientHelper adminClientHelper;

  @BeforeEach
  public void before() {
    this.adminClient = mock(AdminClient.class, Mockito.CALLS_REAL_METHODS);
    this.adminClientHelper = new AdminClientHelper(this.adminClient);
  }

//  <T> KafkaFuture<T> futureOf(T value) {
//    KafkaFuture<T> future = new KafkaFutureImpl<>();
//
//  }

  @Test
  public void findUnderReplicatedPartitions() throws Exception {



    Multimap<Integer, TopicPartition> results = this.adminClientHelper.findUnderReplicatedPartitions(60, TimeUnit.SECONDS);



    assertNotNull(results);

  }


}
