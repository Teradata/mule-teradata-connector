/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration.connectivity;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.extension.db.integration.TestDbConfig.getTeradataResource;

import org.junit.Ignore;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.functional.api.component.EventCallback;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.event.CoreEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DataSourcePoolingTestCase extends AbstractDbIntegrationTestCase {

  private static final int TIMEOUT = 10;
  private static final TimeUnit TIMEOUT_UNIT = SECONDS;
  private static final String DB_NAME_KEY = "db.name";
  private static final String DB_NAME_VALUE = "target/muleEmbeddedDB";
  private static CountDownLatch connectionLatch;

  @Parameterized.Parameters(name = "{2}")
  public static List<Object[]> parameters() {
    return getTeradataResource();
  }

  @Before
  public void setUp() {
    System.setProperty(DB_NAME_KEY, DB_NAME_VALUE);
    setConcurrentRequests(2);
  }

  protected void setConcurrentRequests(int count) {
    connectionLatch = new CountDownLatch(count);
  }

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/connectivity/teradata-db-pooling-config.xml",
        "integration/connectivity/connection-pooling-config.xml"};
  }

  @Test
  public void providesMultipleConnections() throws Exception {
    assertThat(countSuccesses(request("queryAndJoin", 2)), is(2));
  }

  @Test
  public void providesAdditionalPoolingProfileProperties() throws Exception {
    Message[] responses = request("queryAndJoinPoolWithAdditionalProperties", 2);
    assertThat(countSuccesses(responses), is(1));
    assertThat(countFailures(responses), is(1));
  }

  @Test
  public void connectionsGoBackToThePool() throws Exception {
    providesMultipleConnections();
    providesMultipleConnections();
  }

  //TODO: Fix the test for connections
  public void limitsConnections() throws Exception {
    setConcurrentRequests(2);
    Message[] responses = request("queryAndJoinSmallPoolConnections", 2);
    assertThat(countSuccesses(responses), is(1));
    assertThat(countFailures(responses), is(1));
  }

  @Test
  public void limitsConnectionsWithDyanamicConfigs() throws Exception {
    setConcurrentRequests(2);
    Message[] responses = request("queryAndJoinSmallPoolDynamicConnections", 2);
    assertThat(countSuccesses(responses), is(1));
    assertThat(countFailures(responses), is(1));
  }

  protected Message[] request(String flowName, int times) throws Exception {
    Thread[] requests = new Thread[times];
    Message[] responses = new Message[times];

    range(0, times).forEach(i -> {
      requests[i] = new Thread(() -> doRequest(flowName, responses, i));
      requests[i].start();
    });

    for (int i = 0; i < times; i++) {
      requests[i].join();
    }

    return responses;
  }


  @Test
  public void waitForever() throws Exception {
    setConcurrentRequests(3);
    for (int i = 0; i < 3; i++) {
      new Thread(() -> doRunFlow("waitForever")).start();
    }
    assertThat(connectionLatch.await(5, SECONDS), is(false));
  }

  private void doRequest(String flowName, Message[] responses, int index) {
    try {
      responses[index] = doRunFlow(flowName);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Message doRunFlow(String flowName) {
    try {
      CoreEvent response = flowRunner(flowName).run();
      return response != null ? response.getMessage() : null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected int countSuccesses(Message... messages) {
    return count(message -> message.getPayload().getValue().equals("OK"), messages);
  }

  protected int countFailures(Message... messages) {
    return count(message -> message.getPayload().getValue().equals("FAIL"), messages);
  }

  private int count(Predicate<Message> predicate, Message... messages) {
    return new Long(Stream.of(messages).filter(predicate).count()).intValue();
  }

  public static class JoinRequests implements EventCallback {

    @Override
    public void eventReceived(CoreEvent event, Object component, MuleContext muleContext) throws Exception {
      connectionLatch.countDown();

      try {
        connectionLatch.await(TIMEOUT, TIMEOUT_UNIT);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
