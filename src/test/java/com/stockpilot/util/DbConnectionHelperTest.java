package com.stockpilot.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DbConnectionHelperTest {

    @BeforeEach
    public void setUp() {
        // Run database initialization before test
        DbConnectionHelper.initializeSchema();
    }

    @Test
    public void testGetConnection() {
        try (Connection conn = DbConnectionHelper.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("Failed to connect to the database: " + e.getMessage());
        }
    }

    @Test
    public void testSchemaInitialization() throws SQLException {
        try (Connection conn = DbConnectionHelper.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            // Get all user tables
            try (ResultSet rs = metaData.getTables(null, null, null, new String[] { "TABLE" })) {
                Set<String> tableNames = new HashSet<>();
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME").toLowerCase());
                }

                // Assert required tables exist
                assertTrue(tableNames.contains("products"), "Products table should exist");
                assertTrue(tableNames.contains("customers"), "Customers table should exist");
                assertTrue(tableNames.contains("orders"), "Orders table should exist");
                assertTrue(tableNames.contains("order_items"), "Order_items table should exist");
            }
        }
    }
}
