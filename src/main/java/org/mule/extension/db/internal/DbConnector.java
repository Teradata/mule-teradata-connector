/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.internal;

import static org.mule.runtime.api.meta.Category.CERTIFIED;

import org.mule.db.commons.AbstractDbConnector;
import org.mule.db.commons.api.exception.connection.ConnectionCreationException;
import org.mule.db.commons.api.exception.connection.DbError;
import org.mule.db.commons.internal.DbFunctions;
import org.mule.db.commons.internal.exception.DbExceptionHandler;
import org.mule.extension.db.api.logger.LoggerApiPackage;
import org.mule.extension.db.api.param.BulkQueryDefinition;
import org.mule.extension.db.api.param.QueryDefinition;
import org.mule.extension.db.api.param.StoredProcedureCall;
import org.mule.extension.db.internal.domain.connection.teradata.TeradataConnectionProvider;
import org.mule.extension.db.internal.operation.DbBulkOperations;
import org.mule.extension.db.internal.operation.DbDdlOperations;
import org.mule.extension.db.internal.operation.DbDmlOperations;
import org.mule.extension.db.internal.source.RowListener;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.ExpressionFunctions;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.OnException;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

/**
 * The Anypoint Database Connector allows you to connect to relational databases through the JDBC API.
 *
 * @since 1.0
 */
@Extension(name = "Teradata", category = CERTIFIED)
@Operations({DbBulkOperations.class, DbDdlOperations.class, DbDmlOperations.class})
@Sources(RowListener.class)
@ConnectionProviders({TeradataConnectionProvider.class})
@Xml(prefix = "teradata")
@ErrorTypes(DbError.class)
@ExpressionFunctions(DbFunctions.class)
@OnException(DbExceptionHandler.class)
@Export(
    classes = {QueryDefinition.class, StoredProcedureCall.class, BulkQueryDefinition.class, ConnectionCreationException.class,
        LoggerApiPackage.class})
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class DbConnector extends AbstractDbConnector {

}
