/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration.connectivity;

import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.registerDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.runtime.core.api.event.CoreEvent;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized;

//TODO: Original test for Derby DB only. Re-added test if Teradata supports
// Stale Pooled Connection Reset.
@Ignore
public class StalePooledConnectionResetTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/connectivity/teradata-db-pooling-config.xml"};
  }

  @Test
  public void resetsStalePooledConnection() throws Exception {
    CoreEvent response = runFlow("main");
    assertThat(response.getError().isPresent(), is(false));

    stopDatabase();
    startDatabase();

    // Must process the second request without errors
    response = runFlow("main");
    assertThat(response.getError().isPresent(), is(false));
  }

  private void startDatabase() throws SQLException {
    registerDriver(new com.teradata.jdbc.TeraDriver());
  }

  private void stopDatabase() {
    try {
      getConnection("jdbc:teradata:;shutdown=true");
      fail("Expected to throw an exception while shutting down the database");
    } catch (Exception e) {
      // Expected
    }
  }
}
