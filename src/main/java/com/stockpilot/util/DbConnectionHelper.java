package com.stockpilot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Utility class to handle database connections and schema initialization.
 */
public class DbConnectionHelper {
    private static final Logger logger = LoggerFactory.getLogger(DbConnectionHelper.class);

    private static final String DB_URL = "jdbc:h2:./db/stockpilot;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    // Load JDBC Driver class
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load H2 JDBC Driver", e);
            throw new RuntimeException("H2 JDBC Driver not found", e);
        }
    }

    /**
     * Establishes and returns a connection to the database.
     *
     * @return a active Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Initializes the database schema using resources/schema.sql.
     */
    public static void initializeSchema() {
        logger.info("Initializing database schema...");
        try (InputStream is = DbConnectionHelper.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) {
                logger.error("schema.sql file not found in resources folder");
                throw new RuntimeException("schema.sql not found");
            }

            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            // Split statements by semicolon, being careful to avoid splitting on other content.
            // A simple split by ";" will suffice for our schema.sql if we clean empty strings.
            String[] statements = sql.split(";");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                
                for (String statementText : statements) {
                    String trimmedStatement = statementText.trim();
                    if (!trimmedStatement.isEmpty()) {
                        logger.debug("Executing SQL statement: {}", trimmedStatement);
                        stmt.execute(trimmedStatement);
                    }
                }
                logger.info("Database schema initialized successfully.");
            }
        } catch (Exception e) {
            logger.error("Error during database schema initialization", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}
