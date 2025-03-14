/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.db.commons.internal.util.CredentialsMaskUtils.maskUrlUserAndPassword;

import org.junit.Test;

public class CredentialsMaskUtilTestCase {

  private static final String URL_TEST =
      "jdbc:teradata://1.1.1.1:1443;databaseName=STAGING;user=mulesoftuser;password=mulesoftpass;";
  private static final String EXPECTED_URL_TEST_MASKED =
      "jdbc:teradata://1.1.1.1:1443;databaseName=STAGING;user=<<user>>;password=<<credentials>>;";

  @Test
  public void whenUrlWithUserAndPasswordMaskUserPassword() {
    String maskedUrl = maskUrlUserAndPassword(URL_TEST);
    assertThat(maskedUrl, equalTo(EXPECTED_URL_TEST_MASKED));
  }
}
