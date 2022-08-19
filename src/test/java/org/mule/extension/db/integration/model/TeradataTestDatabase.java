/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.integration.model;

import org.mule.extension.db.integration.DbTestUtil;
import org.mule.metadata.api.model.MetadataType;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TeradataTestDatabase extends AbstractTestDatabase {

  @Override
  public DbTestUtil.DbType getDbType() {
    return DbTestUtil.DbType.TERADATA;
  }

  public static String SQL_CREATE_SP_UPDATE_TEST_TYPE_1 = "CREATE PROCEDURE updateTestType1()\n" + "BEGIN\n"
      + "    UPDATE PLANET SET NAME='Mercury' WHERE PLANET_POS=4;\n" + "END;";

  public static String SQL_CREATE_SP_GET_RECORDS =
      "CREATE PROCEDURE getTestRecords()\n" +
          "BEGIN\n" +
          "DECLARE cur1 CURSOR WITH RETURN ONLY FOR\n" +
          "SELECT * FROM PLANET;\n" +
          "open cur1;\n" +
          "END;";

  public static String SQL_CREATE_SP_GET_SPLIT_RECORDS = "CREATE PROCEDURE getSplitTestRecords()\n" +
      "BEGIN\n" +
      "DECLARE cur1 CURSOR WITH RETURN ONLY FOR\n" +
      "SELECT * FROM PLANET WHERE PLANET_POS <= 2;\n" +
      "DECLARE cur2 CURSOR WITH RETURN ONLY FOR\n" +
      "SELECT * FROM PLANET WHERE PLANET_POS > 2;\n" +
      "OPEN cur1;\n" +
      "OPEN cur2;\n" +
      "END;\n";

  @Override
  public void grantUserAllAccess(DataSource dataSource) throws SQLException {
    executeDdl(dataSource,
               "GRANT ALL\n" +
                   "ON INTEGRATION_TEST\n" +
                   "TO dbc;\n COMMIT WORK; \n");
  }

  @Override
  public void createPlanetTable(Connection connection) throws SQLException {
    try {
      executeDdl(connection, "DROP TABLE LOB_TEMP_TABLE;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 3807")) {
        throw e;
      }
    }
    executeDdl(connection, "create global temporary table LOB_TEMP_TABLE(\n" +
        "          id integer not null,\n" +
        "          bval blob,\n" +
        "          cval clob character set unicode)\n" +
        "  unique primary index upi_JdbcLobUpdate(id)\n" +
        "  on commit preserve rows;\n COMMIT WORK; \n");

    try {
      executeDdl(connection, "DROP TABLE PLANET;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 3807")) {
        throw e;
      }
    }
    executeDdl(connection,
               "CREATE TABLE PLANET(ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1\n" +
                   "                                               INCREMENT BY 1\n" +
                   "                                               MINVALUE 0\n" +
                   "                                               MAXVALUE 1000000\n" +
                   "                                               NO CYCLE),PLANET_POS INTEGER,NAME VARCHAR(255), PICTURE BLOB, DESCRIPTION VARCHAR(1000), PRIMARY KEY (ID));\n COMMIT WORK; \n");
  }

  @Override
  public void createSpaceshipTable(Connection connection) throws SQLException {
    try {
      executeDdl(connection, "DROP TABLE SPACESHIP;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 3807")) {
        throw e;
      }
    }
    executeDdl(connection,
               "CREATE TABLE SPACESHIP(ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1\n" +
                   "                                               INCREMENT BY 1\n" +
                   "                                               MINVALUE 0\n" +
                   "                                               MAXVALUE 1000000\n" +
                   "                                               NO CYCLE), MODEL VARCHAR(255), MANUFACTURER VARCHAR(255), PRIMARY KEY (ID));\n COMMIT WORK; \n");
  }

  @Override
  public void createLanguagesTable(Connection connection) throws SQLException {
    try {
      executeDdl(connection, "DROP TABLE LANGUAGES;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 3807")) {
        throw e;
      }
    }
    executeDdl(connection,
               "CREATE TABLE LANGUAGES (NAME VARCHAR(128), SAMPLE_TEXT LONG VARCHAR CHARACTER SET UNICODE);\n COMMIT WORK; \n");
  }

  @Override
  public void createMathFunctionSchema(Connection connection) throws SQLException {

    try {
      executeDdl(connection, "DELETE DATABASE mathFunction\n; COMMIT WORK;\n");
      executeDdl(connection, "DROP DATABASE mathFunction\n; COMMIT WORK;\n");

    } catch (SQLException e) {
      // If the database already exists, ignore the error
      if (!e.getMessage().contains("Error 3802")) {
        throw e;
      }
    }

    try {
      executeDdl(connection, "CREATE DATABASE mathFunction\n" +
          "AS PERMANENT = 60e6, -- 60MB\n" +
          "    SPOOL = 120e6; -- 120MB\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the database already exists, ignore the error
      if (!e.getMessage().contains("Error 5612")) {
        throw e;
      }
    }

    try {
      executeDdl(connection,
                 "GRANT ALL\n" +
                     "ON mathFunction\n" +
                     "TO dbc;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
  }

  @Override
  protected String getInsertPlanetSql(String name, int planet_pos) {
    return "INSERT INTO PLANET (PLANET_POS, NAME) VALUES (" + planet_pos + ", '" + name + "');";
  }

  @Override
  public void createStoredProcedureGetRecords(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE getTestRecords;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    createStoredProcedure(dataSource, SQL_CREATE_SP_GET_RECORDS);
  }

  @Override
  public void createStoredProcedureUpdateTestType1(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE updateTestType1;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    createStoredProcedure(dataSource, SQL_CREATE_SP_UPDATE_TEST_TYPE_1);
  }

  @Override
  public void createStoredProcedureGetSpanishLanguageSampleText(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE getSpanishLanguageSample;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql =
        "CREATE PROCEDURE getSpanishLanguageSample(INOUT languageSample LONG VARCHAR)\n" +
            "BEGIN\n" +
            "SELECT SAMPLE_TEXT INTO languageSample FROM LANGUAGES WHERE NAME = 'Spanish';\n" +
            "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureParameterizedUpdatePlanetDescription(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE updatePlanetDescription;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql =
        "CREATE PROCEDURE updatePlanetDescription(IN pName VARCHAR(50), IN pDescription LONG VARCHAR)\n" +
            "BEGIN\n" +
            "update PLANET SET DESCRIPTION=pDescription where NAME = pName;\n" +
            "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureParameterizedUpdateTestType1(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE updateParamTestType1;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql = "CREATE PROCEDURE updateParamTestType1(IN pName VARCHAR(50))\n" +
        "BEGIN\n" +
        "    DECLARE name VARCHAR(50);\n" +
        "    SET name = pName;\n" +
        "    IF pName IS NULL THEN SET name = 'NullLand';\n" +
        "    END IF;\n" +
        "    UPDATE PLANET SET NAME=name WHERE PLANET_POS=4;\n" +
        "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureCountRecords(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE countTestRecords;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }

    final String sql = "CREATE PROCEDURE countTestRecords(OUT pResult INT)\n" + "BEGIN\n"
        + "select count(*) into pResult from PLANET;\n" + "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureConcatenateStringDate(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE concatStringDate;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }

    final String sql =
        "CREATE PROCEDURE concatStringDate(IN pDate DATE, IN pText VARCHAR(50), OUT pResult VARCHAR(100))\n"
            + "BEGIN\n"
            + " SET pResult = CONCAT(pText, CAST(pDate as VARCHAR(10)));\n"
            + "END;";
    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureConcatenateStringTimestamp(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE concatStringTimestamp;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }

    final String sql =
        "CREATE PROCEDURE concatStringTimestamp(IN pText VARCHAR(50), IN pTimestamp TIMESTAMP, OUT pResult VARCHAR(100))\n"
            + "BEGIN\n"
            + " SET pResult = CONCAT(pText, CAST(pTimestamp as VARCHAR(50)));\n"
            + "END;";
    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureExtractReducedBio(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE getReducedBiography;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }

    final String sql =
        "CREATE PROCEDURE getReducedBiography(IN pName VARCHAR(50), IN pBirthDate DATE, IN pPlaceBirth VARCHAR(50), IN _timestamp TIMESTAMP, IN pPlaceDeath VARCHAR(50), IN pProfession VARCHAR(50), IN pAlmaMater VARCHAR(50), IN pNationality VARCHAR(50), IN pChildren INTEGER, IN pSpouse VARCHAR(50), IN father VARCHAR(50), IN mother VARCHAR(50), IN bio VARCHAR(500), OUT pResult VARCHAR(100))\n"
            + "BEGIN\n"
            + " SET pResult = CONCAT(pName, ' was born ', CAST(pBirthDate as VARCHAR(10)), ', in ', pPlaceBirth, ' and died in ', pPlaceDeath);\n"
            + "END;";
    createStoredProcedure(dataSource, sql);
  }


  @Override
  public void createStoredProcedureGetSplitRecords(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE getSplitTestRecords;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    createStoredProcedure(dataSource, SQL_CREATE_SP_GET_SPLIT_RECORDS);
  }

  @Override
  public void createStoredProcedureDoubleMyInt(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE doubleMyInt;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql =
        "CREATE PROCEDURE doubleMyInt(INOUT pInt INT)\n" + "BEGIN\n" + "SET pInt = pInt * 2;\n" + "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureAddOne(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE mathFunction.addOne;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }

    final String sql = "CREATE PROCEDURE mathFunction.addOne(INOUT num INT)\n" +
        "BEGIN\n" +
        "  SET num = num + 1;\n" +
        "END;";
    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureAddOneDefaultSchema(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE addOne;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    String sql = "CREATE PROCEDURE addOne(INOUT num INT)\n" +
        "BEGIN\n" +
        "  SET num = num - 1;\n" +
        "END;";
    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureMultiplyInts(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE multiplyInts;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql =
        "CREATE PROCEDURE multiplyInts(IN pInt1 INT, IN pInt2 INT, OUT pResult1 INT, IN pInt3 INT, OUT pResult2 INT)\n"
            + "BEGIN\n" + "SET pResult1 = pInt1 * pInt2;\n" + "SET pResult2 = pInt1 * pInt2 * pInt3;\n" + "END;";
    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void returnNullValue(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE returnNullValue;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql =
        "CREATE PROCEDURE returnNullValue(IN pString1 VARCHAR(50), IN pString2 VARCHAR(50), OUT pResult VARCHAR(100))\n"
            + "BEGIN\n" + "SET pResult = null;\n" + "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createStoredProcedureConcatenateStrings(DataSource dataSource) throws SQLException {
    try {
      executeDdl(dataSource, "DROP PROCEDURE concatenateStrings;\n COMMIT WORK; \n");
    } catch (SQLException e) {
      // If the user already exists, ignore the error
      if (!e.getMessage().contains("Error 5495")) {
        throw e;
      }
    }
    final String sql =
        "CREATE PROCEDURE concatenateStrings(IN pString1 VARCHAR(50), IN pString2 VARCHAR(50), OUT pResult VARCHAR(100))\n"
            + "BEGIN\n" + "SET pResult = CONCAT(pString1, pString2);\n" + "END;";

    createStoredProcedure(dataSource, sql);
  }

  @Override
  public void createDelayFunction(DataSource dataSource) throws SQLException {
    //    try {
    //      executeDdl(dataSource, "DROP FUNCTION DELAY;\n COMMIT WORK; \n");
    //    } catch (SQLException e) {
    //      // If the user already exists, ignore the error
    //      if (!e.getMessage().contains("Error 5589")) {
    //        throw e;
    //      }
    //    }
    //    final String sql =
    //        "CREATE FUNCTION DELAY(seconds INTEGER) RETURNS INTEGER DETERMINISTIC\n" + "BEGIN\n" + " DO SLEEP(seconds * 1000);\n"
    //            + " RETURN 1;\n" + "END;";
    //    createStoredProcedure(dataSource, sql);
  }

  @Override
  public MetadataType getDescriptionFieldMetaDataType() {
    return getStringType();
  }

  @Override
  public String getInsertLanguageSql(String name, String sampleText) {
    return "INSERT INTO LANGUAGES VALUES ('" + name + "', '" + sampleText + "');";
  }

  private MetadataType getStringType() {
    return typeBuilder.stringType().build();
  }
}
