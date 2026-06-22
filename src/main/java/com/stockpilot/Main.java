package com.stockpilot;

import com.stockpilot.util.DbConnectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting StockPilot Inventory & Order Management System...");
        
        try {
            // Initialize database schema
            DbConnectionHelper.initializeSchema();
            logger.info("StockPilot started successfully!");
        } catch (Exception e) {
            logger.error("Failed to start StockPilot", e);
            System.exit(1);
        }
    }
}
