/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import org.mule.extension.db.integration.model.*;

import java.util.ArrayList;
import java.util.List;

public class TestDbConfig {

  static {
    USE_TERADATA = getValueFor("teradata");
  }

  private static boolean USE_TERADATA;

  public static List<Object[]> getResources() {
    USE_TERADATA = true;
    List<Object[]> result = new ArrayList<>();

    result.addAll(getTeradataResource());

    return result;
  }

  public static List<Object[]> getTeradataResource() {
    if (USE_TERADATA) {
      final TeradataTestDatabase teradataTestDataBase = new TeradataTestDatabase();
      return singletonList(new Object[] {"integration/config/teradata-db-config.xml", teradataTestDataBase,
          teradataTestDataBase.getDbType(), emptyList()});
    } else {
      return emptyList();
    }
  }

  private static Boolean getValueFor(String vendor) {
    return Boolean.valueOf(System.getProperty(vendor));
  }
}
