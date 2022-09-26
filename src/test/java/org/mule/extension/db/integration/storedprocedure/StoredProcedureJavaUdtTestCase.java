/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.storedprocedure;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.extension.db.integration.model.RegionManager.SOUTHWEST_MANAGER;

import org.junit.Ignore;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.runtime.api.message.Message;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

//TODO: Original test for Derby DB only. Re-added test if Teradata supports UDT.
@Ignore
public class StoredProcedureJavaUdtTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/storedprocedure/stored-procedure-udt-config.xml"};
  }

  @Before
  public void setupStoredProcedure() throws Exception {
    testDatabase.createStoredProcedureGetManagerDetails(getDefaultDataSource());
  }

  @Test
  public void returnsObject() throws Exception {
    Message response = flowRunner("returnsObject").run().getMessage();
    assertThat(response.getPayload().getValue(), equalTo(SOUTHWEST_MANAGER.getContactDetails()));
  }
}
