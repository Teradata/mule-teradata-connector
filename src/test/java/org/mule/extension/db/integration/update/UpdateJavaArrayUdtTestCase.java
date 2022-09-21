/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration.update;

import static java.util.Collections.emptyList;
import static org.mule.extension.db.AllureConstants.DbFeature.DB_EXTENSION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.Ignore;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.extension.db.integration.model.ContactDetails;
import org.mule.runtime.api.message.Message;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runners.Parameterized;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

//TODO: Original test for Derby DB only. Re-added test if Teradata supports UDT.
@Ignore
@Feature(DB_EXTENSION)
@Story("Update Statement")
public class UpdateJavaArrayUdtTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/update/update-udt-array-config.xml"};
  }

  @Test
  public void updatesStringArray() throws Exception {
    Message response = flowRunner("updatesStringArray").run().getMessage();
    assertThat(response.getPayload().getValue(), Matchers.<Object>equalTo(new Object[] {"93101", "97201", "99210"}));
  }

  @Test
  public void updatesMappedObjectArray() throws Exception {
    Message response = flowRunner("updatesStructArray").run().getMessage();

    assertThat(response.getPayload().getValue(), instanceOf(Object[].class));
    final Object[] arrayPayload = (Object[]) response.getPayload().getValue();

    assertThat(arrayPayload.length, is(1));
    assertThat(arrayPayload[0], equalTo(new ContactDetails("work", "2-222-222", "2@2222.com")));
  }

}
