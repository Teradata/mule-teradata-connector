/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.unit;

import org.junit.Before;
import org.junit.Test;
import org.mule.extension.db.internal.domain.connection.teradata.TeradataConnectionParameters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TeradataConnectionParametersUnitTestCase {

  private TeradataConnectionParameters teradataConnectionParameters;

  @Before
  public void setUpTeradataConnectionParameters() {
    teradataConnectionParameters = new TeradataConnectionParameters();
    teradataConnectionParameters.setUrl("jdbc:teradata://mule-teradata-connector-uowav7ypz8ap6h4q.env.clearscape.teradata.com/DATABASE=dbc,DBS_PORT=1025");
    teradataConnectionParameters.setUser("dbc");
    teradataConnectionParameters.setPassword("teradata123");
  }

  @Test
  public void defaultDriverClassName() {
    assertThat(teradataConnectionParameters.getDriverClassName(), is("com.teradata.jdbc.TeraDriver"));
  }

  @Test
  public void defaultUrl() {
    validate("jdbc:teradata://mule-teradata-connector-uowav7ypz8ap6h4q.env.clearscape.teradata.com/DATABASE=dbc,DBS_PORT=1025");
  }

  @Test
  public void defaultUser() {
    assertThat(teradataConnectionParameters.getUser(), is("dbc"));
  }

  @Test
  public void defaultPassword() {
    assertThat(teradataConnectionParameters.getPassword(), is("teradata123"));
  }


  public void validate(String expectedUrl) {
    assertThat(teradataConnectionParameters.getUrl(), is(expectedUrl));
  }

}
