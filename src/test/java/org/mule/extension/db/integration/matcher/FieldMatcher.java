/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.matcher;


import static org.mule.extension.db.integration.model.FieldUtils.getValueAsString;
import org.mule.extension.db.integration.model.Field;
import org.mule.extension.db.integration.model.Record;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class FieldMatcher extends TypeSafeMatcher<Record> {

  private final Field field;

  public FieldMatcher(Field field) {
    this.field = field;
  }

  @Override
  public boolean matchesSafely(Record item) {
    return item.getFields().contains(field);
  }

  public void describeTo(Description description) {
    description.appendText(
                           "Does not contains a field with name = " + field.getName() + " with value = "
                               + getValueAsString(field.getValue()));
  }

  public static Matcher<Record> containsField(Field field) {
    return new FieldMatcher(field);
  }
}
