/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.integration.update;

import static org.mule.extension.db.AllureConstants.DbFeature.DB_EXTENSION;
import static org.mule.extension.db.integration.DbTestUtil.selectData;
import static org.mule.extension.db.integration.TestRecordUtil.assertRecords;
import org.mule.extension.db.api.StatementResult;
import org.mule.extension.db.integration.AbstractDbIntegrationTestCase;
import org.mule.extension.db.integration.model.Field;
import org.mule.extension.db.integration.model.Record;
import org.mule.runtime.api.message.Message;

import org.junit.Test;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import java.util.List;
import java.util.Map;

@Feature(DB_EXTENSION)
@Story("Update Statement")
public class UpdateNameParamOverrideTestCase extends AbstractDbIntegrationTestCase {

  @Override
  protected String[] getFlowConfigurationResources() {
    return new String[] {"integration/update/update-name-param-override-config.xml"};
  }

  public void usesInlineOverriddenParams() throws Exception {
    Message response = flowRunner("inlineOverriddenParams").run().getMessage();

    assertAffectedRows((StatementResult) response.getPayload().getValue(), 1);
    List<Map<String, String>> result = selectData("select * from PLANET where PLANET_POS=3", getDefaultDataSource());
    assertRecords(result, new Record(new Field("NAME", "Mercury"), new Field("PLANET_POS", 3)));
  }

  @Test
  public void usesParamsInInlineQuery() throws Exception {
    Message response = flowRunner("inlineQuery").run().getMessage();

    assertAffectedRows((StatementResult) response.getPayload().getValue(), 1);
    List<Map<String, String>> result = selectData("select * from PLANET where PLANET_POS=4", getDefaultDataSource());
    assertRecords(result, new Record(new Field("NAME", "Mercury"), new Field("PLANET_POS", 4)));
  }

  @Test
  public void usesExpressionParam() throws Exception {
    Message response = flowRunner("expressionParam").withVariable("PLANET_POS", 3).run().getMessage();
    assertAffectedRows((StatementResult) response.getPayload().getValue(), 1);

    List<Map<String, String>> result = selectData("select * from PLANET where PLANET_POS=3", getDefaultDataSource());
    assertRecords(result, new Record(new Field("NAME", "Mercury"), new Field("PLANET_POS", 3)));
  }
}
