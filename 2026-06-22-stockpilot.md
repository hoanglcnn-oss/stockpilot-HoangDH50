# StockPilot Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a robust, layered console-based inventory and order management application called StockPilot that handles product and customer registration, transactional order checkouts, data importing/exporting, stream-based analytics, and concurrent flash-sale thread safety.

**Architecture:** Follow a clean layered architecture splitting the app into domain models (encapsulation), data access repositories (generic interface pattern, JDBC SQL queries only live here), services (business logic, transaction orchestration), utilities (regex validators, connection pooling), and input/output handlers (file operations).

**Tech Stack:** Java 17+, Maven, H2 database (file mode), JUnit 5, SLF4J/Logback (optional logging).

---

## 9-Day Lộ trình Triển khai (Daily Roadmap)

### Day 1: Setup Dự án & H2 Database Initializer
- **Step 1:** Create `pom.xml` with dependencies for H2, JUnit 5, and configuration for packaging as a runnable JAR.
- **Step 2:** Write `schema.sql` defining `products`, `customers`, `orders`, and `order_items` tables with constraints.
- **Step 3:** Implement `DbConnectionHelper.java` utilizing H2 connection string and handling database schema initialization on startup.

### Day 2: Domain Model & Generic Repository (Product & Customer)
- **Step 1:** Create `Product` and `Customer` classes with private properties and validations using Regular Expressions (SKU `^[A-Z]{3}-\d{4}$`, standard email, standard phone).
- **Step 2:** Define `Repository<T, ID>` generic interface (`save`, `findById`, `findAll`, `update`, `deleteById`).
- **Step 3:** Implement JDBC repositories using `PreparedStatement` and try-with-resources.

### Day 3: Order Domain Models & Order Repository
- **Step 1:** Implement `Order` and `OrderItem` models with proper encapsulation.
- **Step 2:** Write `OrderRepository.java` to insert orders and order items, retrieving generated database keys.

### Day 4: Custom Exceptions & Basic OrderService
- **Step 1:** Create custom exceptions inheriting from `RuntimeException` or `Exception` correctly.
- **Step 2:** Implement basic order placement flow: check stock, decrement stock, and save order records.

### Day 5: Transactions & Discount Polymorphism
- **Step 1:** Implement `DiscountPolicy` hierarchy (`NoDiscount`, `PercentageDiscount`, `BulkDiscount`).
- **Step 2:** Implement `@FunctionalInterface PricingRule` using functional lambdas.
- **Step 3:** Implement JDBC atomic transactions (`setAutoCommit(false)`, `commit()`, `rollback()`) inside `OrderService` for placements.

### Day 6: Stream-Based Reports & Lambda Comparators
- **Step 1:** Add analytical methods in `OrderService` utilizing Java Streams (e.g., total revenue, top-N best-selling products, revenue by category, low-stock alerts).
- **Step 2:** Add custom sorting for products and orders via `Comparator` lambdas.

### Day 7: File I/O - CSV Catalog Import & Invoice Export
- **Step 1:** Implement custom manual CSV parsing in `CsvImporter.java` with String split / regex.
- **Step 2:** Implement text document output for invoices and reports under the `output/` directory.

### Day 8: Concurrent Flash-Sale & Background Daemon Export
- **Step 1:** Write `FlashSaleSimulator.java` spawning concurrent ordering threads to demonstrate overselling, then protect with synchronization block / `ReentrantLock`.
- **Step 2:** Implement `AutoExportService.java` as a daemon thread exporting a periodic sales snapshot.

### Day 9: CLI Menu, Unit Tests & Runnable Package
- **Step 1:** Develop the complete CLI console menu in `Main.java`.
- **Step 2:** Create JUnit 5 test suite covering transaction rollbacks and business calculation formulas.
- **Step 3:** Run `mvn clean package` and confirm runnable jar output.
