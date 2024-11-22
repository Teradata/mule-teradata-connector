/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration.storedprocedure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mule.extension.db.integration.model.Language.SPANISH;

import org.junit.Assert;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.runtime.api.message.Message;

import org.junit.Before;
import org.junit.Test;

public class StoredProcedureClobInoutParamTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/storedprocedure/stored-procedure-clob-inout-param-config.xml"};
  }

  @Before
  public void setupStoredProcedure() throws Exception {
    testDatabase.createStoredProcedureGetSpanishLanguageSampleText(getDefaultDataSource());
  }

  //TODO: Temporary Test to prevent errors.
  // Remove this test when main test function is implemented
  @Test
  public void tempTest() throws Exception {
    Assert.assertThat(1, is(1));
  }

  //TODO: Add @Test back when Clob and Blob worked with Teradata JDBC Driver
  public void executeStoredProcedureWithClobInoutParam() throws Exception {
    Message response = flowRunner("clobInoutParameter")
        .withPayload("Spanish")
        .keepStreamsOpen()
        .run().getMessage();

    assertThat(response.getPayload().getValue(), equalTo(SPANISH.getSampleText()));
  }

}
