# Maven Dependency Management
## NiceCommerce Spring Boot - Maven Configuration

This project uses **Maven** exclusively for dependency management and build automation. No Gradle dependencies or configurations are used.

---

## ✅ Maven Configuration

### Project Object Model (POM)

The project uses `pom.xml` as the single source of truth for:
- Dependency management
- Build configuration
- Plugin configuration
- Project metadata

### Maven Version

- **Maven**: 3.9+ (recommended)
- **Java**: 17
- **Spring Boot**: 3.2.0

---

## 📦 Dependency Management

### Maven Parent POM

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

This provides:
- Dependency version management
- Plugin version management
- Default configurations
- Property management

### Dependency Resolution

Maven automatically:
- Downloads dependencies from Maven Central
- Resolves transitive dependencies
- Manages dependency versions
- Handles conflicts

### Dependency Storage

Dependencies are stored in:
- **Local Repository**: `~/.m2/repository/`
- **Remote Repository**: Maven Central (https://repo.maven.apache.org/maven2/)

---

## 🔧 Maven Commands

### Build Commands

```bash
# Clean build artifacts
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Package application (creates JAR)
mvn package

# Install to local repository
mvn install

# Skip tests
mvn install -DskipTests

# Run application
mvn spring-boot:run
```

### Dependency Commands

```bash
# List dependencies
mvn dependency:tree

# Download dependencies
mvn dependency:resolve

# Analyze dependencies
mvn dependency:analyze

# Check for updates
mvn versions:display-dependency-updates
```

### Plugin Commands

```bash
# Generate code coverage report
mvn jacoco:report

# Check code coverage
mvn jacoco:check

# Run security scan
mvn org.owasp:dependency-check-maven:check

# Generate site documentation
mvn site
```

---

## 📋 Maven Wrapper (Optional)

To ensure consistent Maven version across all environments, you can add Maven Wrapper:

```bash
# Install Maven Wrapper
mvn wrapper:wrapper

# Use wrapper instead of mvn
./mvnw clean install
./mvnw spring-boot:run
```

This creates:
- `mvnw` (Unix/Mac)
- `mvnw.cmd` (Windows)
- `.mvn/wrapper/` directory

---

## 🎯 Maven Lifecycle

### Standard Lifecycle Phases

1. **validate** - Validate project
2. **compile** - Compile source code
3. **test** - Run unit tests
4. **package** - Create JAR/WAR
5. **verify** - Run integration tests
6. **install** - Install to local repository
7. **deploy** - Deploy to remote repository

### Common Goals

```bash
# Clean and compile
mvn clean compile

# Clean, compile, and test
mvn clean test

# Clean, compile, test, and package
mvn clean package

# Full lifecycle
mvn clean install
```

---

## 🔍 Dependency Management Features

### Version Management

Versions are managed in `<properties>` section:

```xml
<properties>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
    <jwt.version>0.12.3</jwt.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>
```

### Dependency Scopes

- **compile** (default) - Available in all classpaths
- **provided** - Provided by JDK or container
- **runtime** - Not needed for compilation
- **test** - Only for testing
- **system** - System path (not recommended)

### Exclusions

Exclude transitive dependencies:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.unwanted</groupId>
            <artifactId>dependency</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

## 🛠️ Maven Plugins

### Core Plugins

1. **maven-compiler-plugin** - Compiles Java code
2. **maven-surefire-plugin** - Runs unit tests
3. **maven-failsafe-plugin** - Runs integration tests
4. **spring-boot-maven-plugin** - Packages Spring Boot app

### Additional Plugins

1. **jacoco-maven-plugin** - Code coverage
2. **maven-dependency-plugin** - Dependency management
3. **versions-maven-plugin** - Version updates
4. **maven-enforcer-plugin** - Enforce rules

---

## 📊 Maven Repository Structure

```
~/.m2/repository/
└── com/
    └── nicecommerce/
        └── nicecommerce-springboot/
            └── 1.0.0/
                ├── nicecommerce-springboot-1.0.0.jar
                ├── nicecommerce-springboot-1.0.0.pom
                └── maven-metadata.xml
```

---

## ✅ Verification

### Check Maven Installation

```bash
mvn --version
```

Expected output:
```
Apache Maven 3.9.x
Maven home: /usr/share/maven
Java version: 17.x.x
```

### Verify Project Structure

```bash
# Check POM exists
ls -la pom.xml

# Verify no Gradle files
ls -la build.gradle* settings.gradle* gradlew* 2>/dev/null || echo "No Gradle files found ✅"
```

### Build Verification

```bash
# Clean build
mvn clean install

# Verify JAR created
ls -lh target/*.jar
```

---

## 🚫 No Gradle Support

This project **does NOT use Gradle**:

- ❌ No `build.gradle` files
- ❌ No `settings.gradle` files
- ❌ No `gradlew` wrapper
- ❌ No Gradle dependencies
- ✅ **Maven only** (`pom.xml`)

---

## 📚 Maven Best Practices

1. **Use Parent POM** - Leverage Spring Boot parent
2. **Version Properties** - Centralize version management
3. **Dependency Scopes** - Use appropriate scopes
4. **Plugin Management** - Configure plugins in POM
5. **Profile Management** - Use profiles for environments
6. **Repository Management** - Configure repositories if needed
7. **Exclude Unused Dependencies** - Keep dependencies minimal
8. **Regular Updates** - Update dependencies regularly

---

## 🔄 Maven vs Gradle

| Feature | Maven | Gradle |
|---------|-------|--------|
| Configuration | XML (pom.xml) | Groovy/Kotlin DSL |
| Performance | Good | Faster (incremental) |
| Learning Curve | Moderate | Steeper |
| Plugin Ecosystem | Extensive | Extensive |
| **This Project** | ✅ **Maven** | ❌ Not Used |

---

## 📖 Additional Resources

- [Maven Documentation](https://maven.apache.org/guides/)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/)
- [Maven Best Practices](https://maven.apache.org/guides/introduction/introduction-to-best-practices.html)

---

**Status**: ✅ **Maven Only**  
**Gradle**: ❌ **Not Used**  
**Build Tool**: **Maven 3.9+**

