/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration.select;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;

import javax.sql.DataSource;

import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.mule.extension.db.integration.model.TeradataTestDatabase;

public class SelectExceptionTestCase extends AbstractDbIntegrationTestCase {

  private static final int POOL_CONNECTIONS = 2;

  @Parameterized.Parameters(name = "{2}")
  public static List<Object[]> parameters() {
    final TeradataTestDatabase teradataTestDatabase = new TeradataTestDatabase();
    return singletonList(new Object[] {"integration/config/teradata-db-config.xml", teradataTestDatabase,
        teradataTestDatabase.getDbType(), emptyList()});
  }

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/select/select-exception-config.xml"};
  }

  @Override
  protected DataSource getDefaultDataSource() {
    return getDataSource("pooledJdbcConfig");
  }

  @Test
  public void streamingException() throws Exception {
    for (int i = 0; i < POOL_CONNECTIONS + 1; ++i) {
      try {
        flowRunner("selectException").run();
        fail("Expected 'Table does not exist' exception.");
      } catch (Exception e) {
        assertThat("Iteration " + i, e.getMessage(), containsString("Object 'NOT_EXISTS' does not exist."));
      }
    }
  }
}
