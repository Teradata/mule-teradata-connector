/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.update;

import static java.util.Collections.emptyList;
import static org.mule.extension.db.integration.model.RegionManager.SOUTHWEST_MANAGER;
import static org.mule.extension.db.AllureConstants.DbFeature.DB_EXTENSION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Ignore;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.runtime.api.message.Message;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

//TODO: Original test for Derby DB only. Re-added test if Teradata supports UDT.
@Ignore
@Feature(DB_EXTENSION)
@Story("Update Statement")
public class UpdateStructUdtTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/update/update-udt-config.xml"};
  }

  @Test
  public void updatesWithStruct() throws Exception {
    Message response = flowRunner("updatesWithStruct").run().getMessage();
    assertThat(response.getPayload().getValue(), equalTo(SOUTHWEST_MANAGER.getContactDetails()));
  }

  @Test
  public void updatesWithObject() throws Exception {
    Message response = flowRunner("updatesWithObject").withPayload(SOUTHWEST_MANAGER.getContactDetails()).run().getMessage();
    assertThat(response.getPayload().getValue(), equalTo(SOUTHWEST_MANAGER.getContactDetails()));
  }
}
