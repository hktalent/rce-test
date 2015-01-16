/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.sql.execution;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;


/**
 * Component to read data from a SQL database.
 * 
 * @author Christian Weiss
 */
public class SQLReaderComponent extends AbstractSQLComponent {

    private static final Pattern[] ALLOWED_PATTERNS = new Pattern[] {
        Pattern.compile("^SELECT\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
        , Pattern.compile("^CREATE\\s+TABLE\\s+[^\\s]+\\s+(?:AS\\s+)?SELECT.*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    };

    @Override
    protected void validate() {
        final String sqlQuery = super.getSqlQuery(false);
        boolean valid = false;
        for (final Pattern pattern : ALLOWED_PATTERNS) {
            if (pattern.matcher(sqlQuery).matches()) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            final StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int index = 0; index < ALLOWED_PATTERNS.length; ++index) {
                if (index > 0) {
                    builder.append(", ");
                }
                builder.append(ALLOWED_PATTERNS[index]);
            }
            builder.append("]");
            throw new RuntimeException(String.format("Statement of SQL reader component does not have a valid pattern (%s).",
                builder.toString()));
        }
    }

    @Override
    protected void runSql(final String sql) {
        // declare the jdbc assets to be able to close them in the finally-clause
        Connection jdbcConnection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        // exectue the jdbc stuff
        try {
            jdbcConnection = getConnection();
            statement = jdbcConnection.createStatement();
            statement.execute(sql);
            resultSet = statement.getResultSet();
            distributeResults(resultSet);
        } catch (SQLException e) {
            logger.error("SQL Exception occured.", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                final String message = "ResultSet or Statement could not be closed properly:";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

}
