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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TeradataConnectionParametersUnitTestCase {

  private TeradataConnectionParameters teradataConnectionParameters;

  private static String dbUrl = "";

  private static String dbPort = "";
  private static String dbUser = "";
  private static String dbPassword = "";
  private static String dbDatabase = "";

  @Before
  public void setUpTeradataConnectionParameters() {
    Properties properties = new Properties();

    try (FileInputStream fis = new FileInputStream("src/test/resources/automation-credentials.properties")) {

      properties.load(fis);

      dbUrl = properties.getProperty("INTEGRATION_TEST_HOST");

      dbPort = properties.getProperty("INTEGRATION_TEST_PORT");

      dbUser = properties.getProperty("INTEGRATION_TEST_USERNAME");

      dbPassword = properties.getProperty("INTEGRATION_TEST_PASSWORD");

      dbDatabase = properties.getProperty("INTEGRATION_TEST_DATABASE");

    } catch (IOException e) {

      System.err.println("Error loading configuration: " + e.getMessage());

    }

    teradataConnectionParameters = new TeradataConnectionParameters();
    teradataConnectionParameters
        .setUrl("jdbc:teradata://" + dbUrl + "/DATABASE=" + dbDatabase + ",DBS_PORT=" + dbPort);
    teradataConnectionParameters.setUser(dbUser);
    teradataConnectionParameters.setPassword(dbPassword);
  }

  @Test
  public void defaultDriverClassName() {
    assertThat(teradataConnectionParameters.getDriverClassName(), is("com.teradata.jdbc.TeraDriver"));
  }

  @Test
  public void defaultUrl() {
    validate("jdbc:teradata://" + dbUrl + "/DATABASE=" + dbDatabase + ",DBS_PORT=" + dbPort);
  }

  @Test
  public void defaultUser() {
    assertThat(teradataConnectionParameters.getUser(), is(dbUser));
  }

  @Test
  public void defaultPassword() {
    assertThat(teradataConnectionParameters.getPassword(), is(dbPassword));
  }


  public void validate(String expectedUrl) {
    assertThat(teradataConnectionParameters.getUrl(), is(expectedUrl));
  }

}
