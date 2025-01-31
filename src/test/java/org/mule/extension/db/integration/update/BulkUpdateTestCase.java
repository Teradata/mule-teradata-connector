/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.update;

import static org.mule.extension.db.integration.DbTestUtil.selectData;
import static org.mule.extension.db.integration.TestRecordUtil.assertRecords;
import static org.mule.extension.db.integration.model.Planet.EARTH;
import static org.mule.extension.db.integration.model.Planet.MARS;
import static org.mule.extension.db.integration.model.Planet.VENUS;
import static org.mule.extension.db.AllureConstants.DbFeature.DB_EXTENSION;
import static java.sql.Statement.SUCCESS_NO_INFO;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.extension.db.integration.model.Field;
import org.mule.extension.db.integration.model.Record;
import org.mule.runtime.api.message.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Feature(DB_EXTENSION)
@Story("Update Statement")
public class BulkUpdateTestCase extends AbstractDbIntegrationTestCase {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/update/bulk-update-config.xml"};
  }

  @Test
  public void bulkUpdate() throws Exception {
    Message response = flowRunner("bulkUpdate").withPayload(values()).run().getMessage();
    assertBulkUpdate(response);
  }

  @Test
  public void bulkUpdateWithOverriddenType() throws Exception {
    Message response = flowRunner("bulkUpdateWithOverriddenType").withPayload(values()).run().getMessage();
    assertBulkUpdate(response);
  }

  @Test
  public void updateBulkAfterSelect() throws Exception {
    Message response = flowRunner("updateBulkAfterSelect").withPayload(values()).run().getMessage();
    assertBulkUpdate(response);
  }

  @Test
  public void updateBulkAfterSelectThrowsSqlException() throws Exception {
    exceptionRule.expect(Exception.class);
    flowRunner("updateBulkAfterSelectThrowsError").withPayload(ids()).run().getMessage();
  }

  private List<Map<String, Object>> values() {
    List<Map<String, Object>> values = new ArrayList<>();
    addRecord(values, "name", VENUS.getName());
    addRecord(values, "name", MARS.getName());
    addRecord(values, "name", EARTH.getName());

    return values;
  }

  private List<Map<String, Object>> ids() {
    List<Map<String, Object>> values = new ArrayList<>();
    addRecord(values, "id", 1);
    addRecord(values, "id", MARS.getName());
    addRecord(values, "id", 1);

    return values;
  }

  private void addRecord(List<Map<String, Object>> values, String key, Object value) {
    Map<String, Object> record = new HashMap<>();
    record.put(key, value);
    values.add(record);
  }

  private void assertBulkUpdate(Message response) throws SQLException {
    assertTrue(response.getPayload().getValue() instanceof int[]);
    int[] counters = (int[]) response.getPayload().getValue();
    assertThat(counters[0], anyOf(equalTo(1), equalTo(SUCCESS_NO_INFO)));
    assertThat(counters[1], anyOf(equalTo(1), equalTo(SUCCESS_NO_INFO)));
    assertThat(counters[2], anyOf(equalTo(1), equalTo(SUCCESS_NO_INFO)));

    List<Map<String, String>> result = selectData("select * from PLANET order by ID", getDefaultDataSource());
    assertRecords(result, new Record(new Field("NAME", "Mercury"), new Field("PLANET_POS", 2)),
                  new Record(new Field("NAME", "Mercury"), new Field("PLANET_POS", 3)),
                  new Record(new Field("NAME", "Mercury"), new Field("PLANET_POS", 4)));
  }
}
