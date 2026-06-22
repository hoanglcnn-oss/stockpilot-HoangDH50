# Long Assignment — "StockPilot" Inventory & Order Management System

> **Code:** JSE-LA-01 · **Duration:** 2 weeks (individual) · **Scoring:** 0–10
> **Integration scope:** OOP (encapsulation, inheritance, abstraction, polymorphism, interfaces) · String & Regex · Collections & Generics · Lambdas & Functional Interfaces · Streams · Exception Handling · File I/O · Multithreading & Synchronization · JDBC (database) · JUnit testing · Maven build.

---

## 1. Context & Objective

You are hired as a backend developer for **StockPilot**, a small distribution company that sells products to retail stores. Their staff currently track inventory and customer orders in spreadsheets — it is error-prone, products go oversold, and there are no reliable sales reports.

Your job: build the **StockPilot** backend — a **console (CLI) Java application**, built with **Maven**, that manages products and stock, processes customer orders, persists everything to a **relational database via JDBC**, imports/exports data via **files**, produces **sales analytics with Streams**, and safely handles **concurrent order processing**.

This is **not** a set of disconnected textbook exercises — it is **one realistic product**. Every Java SE topic you learned must appear naturally where the domain calls for it. After this assignment you can demonstrate that you can design a layered application, model a real domain with OOP, talk to a database, handle failures, and reason about concurrency.

**Important framing:** the focus is on **correct, clean, well-structured Java** — not on a fancy UI. A plain text menu in the console is perfectly fine.

---

## 2. Allowed Technologies

| Scope | Tools |
|---|---|
| Required | **Java 17+**, **Maven** (build, dependencies, package as runnable JAR) |
| Required | **JDBC** with a real database — **H2** (file mode, recommended), **SQLite**, or **MySQL** |
| Required | **JUnit 5** for unit tests |
| Allowed | A logging library (SLF4J/Logback), a CSV library is **optional** — parsing CSV by hand is encouraged for the String/Regex practice |
| Not allowed | Spring / Spring Boot / Hibernate / any ORM or DI framework; a GUI framework; storing data only in memory (data **must** persist in the DB); AI-generated code you cannot explain |

> This module tests **core Java SE**. Frameworks that hide JDBC, threading, or wiring are not allowed — you must write those yourself.

---

## 3. Required Architecture (layered)

You must structure the app into clear layers. This is part of the grade.

```
src/main/java/com/stockpilot/
├── Main.java                  # entry point + CLI menu loop
├── model/                     # domain classes (Product, Order, Customer, ...)
├── repository/                # DAO layer — JDBC only lives here
│   ├── Repository.java        # generic interface Repository<T, ID>
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── service/                   # business logic (pricing, stock rules, reports)
├── exception/                 # custom exceptions
├── io/                        # CSV import/export, file reports
├── concurrent/                # order-processing threads
└── util/                      # validators (regex), DB connection helper

src/test/java/com/stockpilot/  # JUnit tests
src/main/resources/            # schema.sql, seed data, config
```

**Rule:** JDBC code (`Connection`, `PreparedStatement`, SQL) appears **only** in the `repository` layer. The `service` layer never touches SQL directly. The CLI never touches the database directly.

---

## 4. Functional Requirements (app features)

The app is organised into the modules below — the features StockPilot's staff actually use. **You must implement all core features F1–F6.** The technical requirements (the Java SE skills you must demonstrate) are woven into each feature as the *Technical requirements* lines; items marked **[Excellent]** are optional and reward higher grades.

### F1 — Product & Inventory Management
The backbone of the system: staff add, view, search, update, and remove products, and adjust stock levels.
- A menu to **create / list / find / update / delete** products and change stock quantity.
- Each product has: id, SKU, name, category, price, stock quantity.

*Technical requirements:*
- **OOP:** model `Product`, `Customer`, `Order`, `OrderItem` with **encapsulation** (private fields, validation in constructors); override `toString()`, `equals()`, `hashCode()` where appropriate.
- **Collections & Generics:** access data through a **generic** `Repository<T, ID>` (`save`, `findById`, `findAll`, `update`, `deleteById`); hold collections with the Collections Framework.
- **Regex:** validate **SKU** (e.g. `^[A-Z]{3}-\d{4}$`) before saving; reject invalid input with a clear message (no crash).
- **JDBC:** persist to a `products` table using **`PreparedStatement`** (no string concatenation); open resources in **try-with-resources**.

### F2 — Customer Management
Register and look up the retail customers who place orders.
- Add and list customers (name, email, phone).

*Technical requirements:*
- **Regex:** validate **email** and **phone** formats.
- **JDBC:** a `customers` table accessed through the same generic repository pattern.

### F3 — Order Processing & Checkout
The core business transaction — building an order and committing it safely.
- Build a cart, pick a customer, apply discounts, place the order; stock is decremented and an invoice is produced.

*Technical requirements:*
- **Collections:** cart as `Map<String, Integer>` (SKU → qty), order lines as `List<OrderItem>`.
- **OOP polymorphism:** pricing via an abstract `DiscountPolicy` (or interface) with subtypes `NoDiscount`, `PercentageDiscount`, `BulkDiscount`, applied **polymorphically**.
- **Lambdas & functional interfaces:** a custom `@FunctionalInterface PricingRule` (or `Function<Order, BigDecimal>`) for discount/pricing rules; **`Comparator`** lambdas to sort products/orders (by price, name, date).
- **JDBC transaction:** placing an order inserts the order + items and decrements stock **atomically** (commit on success, rollback on failure). **[Excellent]** use `BigDecimal` for all money; verify rollback leaves stock/orders unchanged on a forced mid-order failure.
- **Exceptions:** throw and handle `InsufficientStockException`, `ProductNotFoundException`, etc.; the CLI shows a message and returns to the menu (never crashes).

### F4 — Sales Reports & Analytics
Management dashboard: turn the order history into insight.
- Total revenue and number of orders in a period; **top-N best-selling products**; revenue **by category**; **low-stock** products needing reorder.

*Technical requirements:*
- **Streams:** produce every report with the **Stream API** (not manual loops) — e.g. `filter`, `map`, `sorted`, `limit`, and `Collectors.groupingBy` + `summingDouble`/`reducing`.

### F5 — Data Import & Document Export
Onboard the existing catalog and produce printable documents.
- **Import** the initial product catalog from a **CSV file** (parse, validate, insert into the DB).
- **Export** an order's invoice and a sales report to files under an `output/` folder.

*Technical requirements:*
- **File I/O:** read/write text/CSV; handle file-not-found and malformed lines gracefully.
- **String/Regex:** parse and format CSV lines and invoice lines.

### F6 — Flash Sale (concurrent order processing)
A realistic stress scenario: many orders for the same limited-stock product arrive at once.
- Simulate **N concurrent orders** for a limited-stock product using an `ExecutorService` (thread pool).

*Technical requirements:*
- **Multithreading & Synchronization:** ensure stock is **never oversold** — protect the check-and-decrement with `synchronized`, a `ReentrantLock`, or a DB transaction + row locking. Demonstrate that *without* synchronization a race condition oversells, then show your fix (include a short write-up).
- **[Excellent]** a background thread that **auto-exports** a sales snapshot on a schedule, with graceful shutdown.

> **Across all features — exception handling & resources.** Define the custom exceptions `ProductNotFoundException`, `InsufficientStockException`, `InvalidInputException`, and `DataAccessException`; choose checked vs unchecked appropriately; wrap low-level `SQLException` into `DataAccessException`; never swallow exceptions silently; and always open DB/file resources in **try-with-resources**.

---

## 5. Cross-cutting Quality Bar (applies to the whole project)

Beyond the per-feature requirements above, these apply everywhere:

- **Clean architecture & OOP:** respect the layer boundaries from §3 (SQL only in `repository`, no business logic in `Main`); small classes with a single responsibility, no "god class".
- **Type safety:** no raw types, no unchecked-cast warnings.
- **Tests:** at least **8 meaningful JUnit 5 tests** covering service logic (e.g. discount calc, insufficient-stock throws, top-N report) — not trivial getter tests. Use assertions and `assertThrows`.
- **Maven:** `mvn clean package` builds a **runnable JAR**; dependencies (JDBC driver, JUnit) declared in `pom.xml`; tests run in the `test` phase.
- **Git:** meaningful commits across the two weeks (not one giant final commit).
- **No leftover** `printStackTrace()` debugging spam in normal flow, no commented-out dead code.

---

## 6. How You Are Graded (0–10)

The work is graded in three tiers. **Lower tiers must be solid before higher tiers count** — a feature-rich app with broken persistence or oversold stock scores **lower** than a smaller app that is correct. Aim to complete each tier in order.

### Pass tier (foundation must work end-to-end)
The core app runs and persists data. To reach this tier you need:
- Maven project builds (`mvn clean package`) and the JAR runs.
- OOP domain model with encapsulation + at least one abstraction used polymorphically.
- JDBC persistence: products & orders are saved and reloaded from the DB.
- Place-order flow: stock check + decrement + order saved.
- Custom exceptions; the CLI handles errors without crashing.
- Regex validation for SKU/email/phone, and a generic `Repository<T, ID>`.

### Good tier (intermediate techniques)
On top of a solid Pass tier:
- Place-order is a real **transaction** (commit/rollback, atomic stock decrement).
- Stream-based reports (top-N products, revenue by category, low stock).
- CSV import + invoice/report export to files, with error handling.
- Lambdas + a functional interface for pricing/discount rules and comparators.

### Excellent tier (distinguishing, advanced)
On top of a solid Good tier, the work that sets the best submissions apart:
- Concurrent flash-sale simulation with correct synchronization (no oversell) + a short write-up of the race condition and your fix.
- A meaningful JUnit 5 test suite (incl. `assertThrows` and a report-logic test).
- Clean layered architecture (no SQL outside the repository layer, no business logic in `Main`).
- `BigDecimal` for all money calculations; verified transaction rollback; a background auto-export thread with graceful shutdown.
- A complete README (setup/run, schema description, feature checklist).

> **Note.** Practices such as SQL injection (string-concatenated queries), unclosed resources, raw types, data that isn't actually persisted, no Git history, or code you cannot explain in the viva will cost you marks. Write clean, correct, explainable Java.

---

## 7. Submission

1. Push the source to a **public GitHub repo** (name: `stockpilot-<yourname>`).
2. The repo must include: all source, `pom.xml`, `schema.sql`, a sample `products.csv` for import, and a `README.md`.
3. The **README** includes: short description, tech stack, **how to set up the DB and run** (`mvn clean package` + the run command), a description of the schema, the **race-condition write-up**, and a **feature checklist** (mark each Pass/Good/Excellent item done).
4. Submit the repo link per class instructions before the deadline.

**Run target:**
```bash
mvn clean package
java -jar target/stockpilot-1.0.0.jar
```

---

> **Recommendation.** Get the vertical slice working first: *one* product saved to the DB and read back, then place one order in a transaction. Once persistence is solid, layer on reports (streams), files, and finally concurrency. A correct, well-structured smaller app beats a broken feature-heavy one.
