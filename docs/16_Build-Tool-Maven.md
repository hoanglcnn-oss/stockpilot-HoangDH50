# Build Tool — Apache Maven

## 1. What is Maven?
**Maven** is a powerful **build automation and project management tool** for Java projects, maintained by the Apache Software Foundation. It standardizes how a project is built, tested, packaged, and documented.

**What Maven can accomplish:**
- **Build the project** (compile, test, package into JAR/WAR/EAR).
- **Run reports** (test results, code coverage, quality checks).
- **Generate a project web site / documentation.**

> Maven *can do all of these* — build, reporting, and site generation.

**What Maven encompasses (its three pillars):**
1. A **Project Object Model (POM)** — describes the project.
2. A **project lifecycle** — the standard sequence of build phases.
3. A **dependency management system** — automatically downloads and manages libraries.

**Key benefit:** *Convention over configuration* — by following Maven's standard directory layout and conventions, you write minimal configuration and get a repeatable, portable build.

---

## 2. The POM File
**POM** stands for **Project Object Model**. It is the fundamental unit of work in Maven — an XML file named `pom.xml` placed at the root of the project. It describes the project, its dependencies, plugins, and build configuration.

**Maven Coordinates** — the values needed to uniquely identify any project/artifact (and required by an archetype when creating a new project):

| Element | Meaning |
|---------|---------|
| `groupId` | The organization/group identifier (e.g. `com.fsoft.training`). |
| `artifactId` | The **ID/name of the project** — the name of the produced artifact. |
| `version` | The version of the project (e.g. `1.0.0-SNAPSHOT`). |

> When creating a new project from an archetype, **all three** (`groupId`, `artifactId`, `version`) are required.

**Note on `artifactId`:** it serves as both an *id of the project* and *generally the name of the project* — i.e. **both** statements are correct.

**Minimal `pom.xml` example:**
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fsoft.training</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

## 3. Maven Installation & Repositories
A **Maven repository** is a location where project artifacts (libraries, plugins) are stored.

**Three types of Maven repositories:**
1. **Local** — a folder on your own machine that caches downloaded artifacts.
2. **Remote** — any custom/company server (e.g. Nexus, Artifactory) holding artifacts.
3. **Maven Central** — the public default remote repository hosted by Apache.

> ⚠️ "Maven Local" is **not** an official repository type — the correct name is **Local**.

**Default location of the local repository:**
```
~/.m2/repository
```
(`~` is the user's home directory. The `.m2` folder also holds `settings.xml`.)

**How resolution works:** Maven first looks in the **local** repository; if the artifact isn't found, it downloads it from a **remote** repository (Maven Central by default) and caches it locally for next time.

---

## 4. Dependency Management & Scopes
Maven automatically downloads the libraries (dependencies) declared in the POM, including their *transitive* dependencies.

A **dependency scope** controls *when* (in which classpath / lifecycle phase) a dependency is available.

**Valid dependency scopes:**

| Scope | Description |
|-------|-------------|
| **compile** | **Default scope.** Available in all classpaths (compile, test, runtime) and packaged with the app. |
| **provided** | Needed to compile but provided at runtime by the JDK/container (e.g. Servlet API). Not packaged. |
| **runtime** | Not needed for compilation, only at runtime (e.g. a JDBC driver). |
| **test** | Only available for test compilation and execution (e.g. JUnit). |
| **system** | Like `provided` but you point to a JAR on the local filesystem explicitly. |

> ⚠️ **`export` is NOT a valid scope.** If `<scope>` is omitted, Maven uses **compile**.

---

## 5. Maven Build Lifecycle
Maven defines **three built-in (standard) lifecycles**, each made up of an ordered sequence of **phases**. Running a phase runs every phase before it.

### a) `default` lifecycle (builds the project)
Key phases (in order), commonly tested:
1. **validate** — validate the project is correct and all info is available.
2. **compile** — **compiles the source code** of the project.
3. **test** — run unit tests with a testing framework.
4. **package** — package compiled code into its distributable format (JAR/WAR).
5. **verify** — run checks on integration test results.
6. **install** — install the package into the **local** repository.
7. **deploy** — copy the final package to a **remote** repository.

### b) `clean` lifecycle (cleans up)
Has **3 phases**: `pre-clean`, **clean**, `post-clean`.
- The **clean** phase **removes the `target` directory** (with all build output) before a fresh build.

### c) `site` lifecycle (generates documentation/web site)
Phases: `pre-site`, `site`, `post-site`, `site-deploy`.

---

## 6. Maven Plugins
Maven is essentially a **plugin execution framework** — all real work is done by plugins. A plugin provides **goals** (units of work). A **MOJO** = **Maven plain Old Java Object**, the Java class implementing a single goal.

**Two types of Maven plugins:**
1. **Build plugins** — execute during the build (configured in `<build>`), e.g. `maven-compiler-plugin`.
2. **Reporting plugins** — execute during site generation (configured in `<reporting>`), e.g. `maven-surefire-report-plugin`.

> ⚠️ "Remote" is **not** a type of Maven plugin.

---

## 7. Maven Project & Packaging
The `<packaging>` element in the POM tells Maven what artifact to produce.

| Packaging | Produces | Notes |
|-----------|----------|-------|
| **jar** | `.jar` | Default packaging type. |
| **war** | `.war` | Web app — requires a `web.xml` in `src/main/webapp/WEB-INF/`. |
| **ear** | `.ear` | Enterprise application archive. |
| **pom** | — | Used by parent/aggregator projects. |

**Archetypes** are project templates used to bootstrap a new project:
- `maven-archetype-quickstart` → produces a **JAR** project.
- `maven-archetype-webapp` → packaging is **WAR**.

**Standard directory layout:**
```
my-app/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/        # application source
    │   ├── resources/   # config files
    │   └── webapp/      # web resources (WAR projects)
    │       └── WEB-INF/web.xml
    └── test/
        └── java/        # test source
target/                  # build output (removed by mvn clean)
```

---

## 8. Profiles & Settings
- **Profiles** let you customize the build for different environments (dev, test, prod).
- Profiles can be configured in **`pom.xml`** (project-specific) — and also in `settings.xml` (user/global) — but the project-level place is **`pom.xml`**.
- **`settings.xml`** (in `~/.m2/`) holds user/machine-specific configuration: local repo path, mirrors, credentials, and global profiles.

---

## 9. Common Maven Commands
Maven is invoked with the `mvn` command (or `./mvnw`, the **Maven Wrapper**, which runs a pinned Maven version without a system install).

| Command | What it does |
|---------|--------------|
| `mvn -version` | **Check the installed Maven version.** |
| `mvn clean` | Run the clean lifecycle — **removes the `target` directory** before building. |
| `mvn compile` | Compile the source code. |
| `mvn test` | Compile and run unit tests. |
| `mvn package` | **Package the project** into a JAR/WAR (in `target/`). |
| `mvn install` | Build and install the artifact into the local repository. |
| `mvn site` | Generate the project documentation site. |
| `mvn clean package` | Common combo: clean then build the package. |

**Useful command-line options (flags):**

| Flag | Meaning |
|------|---------|
| **`-X`** | Produce **execution debug output** (full debug logging). |
| `-D<name>=<value>` | Define a system property (e.g. `-DskipTests`). |
| `-P<profile>` | Activate a build profile. |
| `-q` | Quiet output. |

> ⚠️ Note the case sensitivity: **`-X`** = debug output, while `-D` defines a property — they are different.

---

## Summary
- **Maven** = build automation + project management; encompasses a **POM**, a **lifecycle**, and **dependency management**.
- **POM** (`pom.xml`) = Project Object Model; identifies a project via **groupId + artifactId + version**.
- **Repositories**: Local (`~/.m2/repository`), Remote, and Maven Central.
- **Scopes**: compile (default), provided, runtime, test, system — *not* `export`.
- **Lifecycles**: `default` (compile → test → package → install → deploy), `clean` (3 phases), `site`.
- **Plugins**: Build & Reporting types; a goal is implemented by a **MOJO** (Maven plain Old Java Object).
- **Packaging**: jar (default), war (needs `web.xml`), ear; `maven-archetype-webapp` → WAR.
- **Commands**: `mvn -version`, `mvn clean`, `mvn package`; debug with **`-X`**.
