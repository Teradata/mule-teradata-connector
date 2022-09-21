/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.storedprocedure;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mule.extension.db.integration.model.Contact.CONTACT1;
import static org.mule.extension.db.integration.model.Region.SOUTHWEST;

import org.junit.Ignore;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.extension.db.integration.DbTestUtil;
import org.mule.extension.db.integration.model.ContactDetails;
import org.mule.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

//TODO: Original test for Derby DB only. Re-added test if Teradata supports UDT.
@Ignore
public class StoredProcedureJavaArrayUdtTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/storedprocedure/stored-procedure-udt-array-config.xml"};
  }

  @Before
  public void setupStoredProcedure() throws Exception {
    final DataSource dataSource = getDefaultDataSource();
    testDatabase.createStoredProcedureGetZipCodes(dataSource);
    testDatabase.createStoredProcedureGetContactDetails(dataSource);
  }

  @Test
  public void returnsDefaultArray() throws Exception {
    Message response = flowRunner("returnsDefaultArrayValue").keepStreamsOpen().run().getMessage();
    List<Object> values = new ArrayList<>(asList((Object[]) response.getPayload().getValue()));
    assertThat(values, hasItems(SOUTHWEST.getZips()));
  }

  @Test
  public void returnsCustomArray() throws Exception {
    Message response = flowRunner("returnsCustomArrayValue").keepStreamsOpen().run().getMessage();
    List<Object> values = new ArrayList<>(asList((Object[]) response.getPayload().getValue()));
    assertThat(values, hasItems(CONTACT1.getDetails()));
  }
}
