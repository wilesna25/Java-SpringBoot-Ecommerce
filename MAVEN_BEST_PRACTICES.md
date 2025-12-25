# Maven Best Practices Guide
## NiceCommerce Spring Boot - Expert Level

Comprehensive guide for using Maven effectively in the NiceCommerce Spring Boot project, following industry best practices and expert recommendations.

---

## 📋 Table of Contents

1. [Project Structure](#project-structure)
2. [POM Configuration](#pom-configuration)
3. [Dependency Management](#dependency-management)
4. [Build Optimization](#build-optimization)
5. [Version Management](#version-management)
6. [Plugin Configuration](#plugin-configuration)
7. [Profiles and Environments](#profiles-and-environments)
8. [Testing Best Practices](#testing-best-practices)
9. [Security Practices](#security-practices)
10. [Performance Optimization](#performance-optimization)
11. [Common Commands](#common-commands)
12. [Troubleshooting](#troubleshooting)

---

## 📁 Project Structure

### Recommended Maven Directory Layout

```
nicecommerce-springboot/
├── pom.xml                          # Main POM file
├── .mvn/                            # Maven wrapper (optional)
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/                    # Main source code
│   │   ├── resources/               # Resources (config files)
│   │   └── webapp/                  # Web resources (if needed)
│   └── test/
│       ├── java/                    # Test source code
│       └── resources/               # Test resources
└── target/                          # Build output (ignored in Git)
    ├── classes/                     # Compiled classes
    ├── test-classes/               # Compiled test classes
    ├── surefire-reports/            # Test reports
    └── nicecommerce-springboot-1.0.0.jar
```

**Best Practice**: Follow Maven's standard directory layout. Don't customize unless absolutely necessary.

---

## ⚙️ POM Configuration

### 1. Use Parent POM

**✅ DO:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>
```

**Why?**
- Inherits dependency versions
- Inherits plugin versions
- Reduces configuration
- Ensures compatibility

### 2. Define Project Metadata

**✅ DO:**
```xml
<groupId>com.nicecommerce</groupId>
<artifactId>nicecommerce-springboot</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>

<name>NiceCommerce Spring Boot</name>
<description>E-commerce platform migrated from Django to Spring Boot</description>
<url>https://github.com/yourusername/nicecommerce-springboot</url>

<licenses>
    <license>
        <name>MIT License</name>
        <url>https://opensource.org/licenses/MIT</url>
    </license>
</licenses>

<developers>
    <developer>
        <name>NiceCommerce Team</name>
        <email>dev@nicecommerce.com</email>
    </developer>
</developers>
```

**Why?**
- Provides project information
- Helps with documentation
- Useful for publishing artifacts

### 3. Use Properties for Versions

**✅ DO:**
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    
    <!-- Dependency versions -->
    <spring-cloud.version>2023.0.0</spring-cloud.version>
    <jwt.version>0.12.3</jwt.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>
```

**❌ DON'T:**
```xml
<!-- Hardcode versions in dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>  <!-- Don't do this -->
</dependency>
```

**Why?**
- Centralized version management
- Easy to update versions
- Reduces duplication
- Prevents version conflicts

### 4. Use Dependency Management

**✅ DO:**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Why?**
- Manages transitive dependencies
- Ensures version consistency
- Reduces conflicts

---

## 📦 Dependency Management

### 1. Use Appropriate Scopes

**✅ DO:**
```xml
<!-- compile (default) - needed at compile and runtime -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- provided - provided by runtime environment -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<!-- runtime - needed at runtime, not compile time -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- test - only for testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Why?**
- Reduces classpath size
- Prevents unnecessary dependencies
- Improves build performance
- Clearer dependency intent

### 2. Exclude Unwanted Transitive Dependencies

**✅ DO:**
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

**Why?**
- Prevents version conflicts
- Reduces JAR size
- Removes unused dependencies
- Improves security (fewer dependencies = smaller attack surface)

### 3. Use BOM (Bill of Materials)

**✅ DO:**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Why?**
- Manages related dependency versions
- Ensures compatibility
- Simplifies version management

### 4. Avoid Version Ranges

**❌ DON'T:**
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <version>[1.0,2.0)</version>  <!-- Don't use ranges -->
</dependency>
```

**✅ DO:**
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <version>1.5.0</version>  <!-- Use specific version -->
</dependency>
```

**Why?**
- Reproducible builds
- Predictable behavior
- Easier debugging
- Prevents unexpected updates

### 5. Check for Updates Regularly

**✅ DO:**
```bash
# Check for dependency updates
mvn versions:display-dependency-updates

# Check for plugin updates
mvn versions:display-plugin-updates

# Update versions interactively
mvn versions:use-latest-versions
```

**Why?**
- Security patches
- Bug fixes
- New features
- Performance improvements

---

## 🚀 Build Optimization

### 1. Use Maven Wrapper

**✅ DO:**
```bash
# Install Maven Wrapper
mvn wrapper:wrapper -Dmaven=3.9.5

# Use wrapper instead of mvn
./mvnw clean install
```

**Why?**
- Consistent Maven version
- No need to install Maven
- Works in CI/CD
- Team uses same version

### 2. Parallel Build Execution

**✅ DO:**
```bash
# Build with parallel threads
mvn clean install -T 4

# Or configure in settings.xml
<settings>
    <parallel>4</parallel>
</settings>
```

**Why?**
- Faster builds
- Better resource utilization
- Reduced build time

### 3. Skip Tests When Appropriate

**✅ DO:**
```bash
# Skip tests during development
mvn clean install -DskipTests

# Skip tests and compilation
mvn clean install -Dmaven.test.skip=true

# But always run tests before committing!
```

**❌ DON'T:**
```bash
# Don't skip tests in CI/CD
# Don't skip tests before committing
# Don't skip tests in production builds
```

### 4. Use Build Profiles

**✅ DO:**
```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

**Usage:**
```bash
mvn clean install -Pdev
mvn clean install -Pprod
```

**Why?**
- Environment-specific builds
- Conditional dependencies
- Flexible configuration

### 5. Optimize Dependency Resolution

**✅ DO:**
```bash
# Download dependencies only
mvn dependency:resolve

# Go offline after downloading
mvn clean install -o

# Use local repository cache
# Maven automatically caches in ~/.m2/repository/
```

**Why?**
- Faster builds
- Works offline
- Reduces network usage

---

## 🔢 Version Management

### 1. Semantic Versioning

**✅ DO:**
```
Version format: MAJOR.MINOR.PATCH

Examples:
- 1.0.0 - Initial release
- 1.0.1 - Bug fix
- 1.1.0 - New feature
- 2.0.0 - Breaking change
```

**Why?**
- Clear version meaning
- Easy to understand changes
- Industry standard

### 2. Use SNAPSHOT for Development

**✅ DO:**
```xml
<version>1.0.0-SNAPSHOT</version>  <!-- Development -->
<version>1.0.0</version>            <!-- Release -->
```

**Why?**
- Distinguishes development from release
- Allows continuous integration
- Prevents accidental releases

### 3. Version Properties

**✅ DO:**
```xml
<properties>
    <project.version>1.0.0</project.version>
    <spring-boot.version>3.2.0</spring-boot.version>
</properties>

<version>${project.version}</version>
```

**Why?**
- Single source of truth
- Easy to update
- Consistent versioning

### 4. Release Management

**✅ DO:**
```bash
# Prepare release
mvn release:prepare

# Perform release
mvn release:perform

# Or use Maven Release Plugin
mvn versions:set -DnewVersion=1.1.0
mvn clean install
git tag v1.1.0
```

**Why?**
- Automated version bumping
- Tag creation
- Release artifacts

---

## 🔌 Plugin Configuration

### 1. Configure Compiler Plugin

**✅ DO:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <encoding>UTF-8</encoding>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Why?**
- Ensures correct Java version
- Configures annotation processors
- Consistent compilation

### 2. Configure Surefire Plugin (Tests)

**✅ DO:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
        <excludes>
            <exclude>**/*IT.java</exclude>
        </excludes>
        <parallel>classes</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

**Why?**
- Faster test execution
- Parallel test runs
- Better test organization

### 3. Configure Failsafe Plugin (Integration Tests)

**✅ DO:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Why?**
- Separate unit and integration tests
- Integration tests run in verify phase
- Better test organization

### 4. Configure Spring Boot Plugin

**✅ DO:**
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Why?**
- Creates executable JAR
- Excludes development dependencies
- Optimizes JAR size

---

## 🌍 Profiles and Environments

### 1. Environment-Specific Profiles

**✅ DO:**
```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    
    <profile>
        <id>test</id>
        <properties>
            <spring.profiles.active>test</spring.profiles.active>
        </properties>
    </profile>
    
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

**Usage:**
```bash
mvn clean install -Pdev
mvn clean install -Ptest
mvn clean install -Pprod
```

### 2. Conditional Dependencies

**✅ DO:**
```xml
<profile>
    <id>dev</id>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
    </dependencies>
</profile>
```

**Why?**
- Environment-specific dependencies
- Smaller production JAR
- Development tools only in dev

### 3. Profile-Specific Resources

**✅ DO:**
```xml
<profile>
    <id>prod</id>
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <includes>
                    <include>application-prod.yml</include>
                </includes>
            </resource>
        </resources>
    </build>
</profile>
```

**Why?**
- Environment-specific configurations
- Secure production settings
- Flexible deployment

---

## 🧪 Testing Best Practices

### 1. Separate Unit and Integration Tests

**✅ DO:**
```xml
<!-- Unit tests: *Test.java -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
        </includes>
    </configuration>
</plugin>

<!-- Integration tests: *IT.java -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
        </includes>
    </configuration>
</plugin>
```

**Why?**
- Clear test separation
- Different execution phases
- Better test organization

### 2. Parallel Test Execution

**✅ DO:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>classes</parallel>
        <threadCount>4</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
    </configuration>
</plugin>
```

**Why?**
- Faster test execution
- Better resource utilization
- Reduced build time

### 3. Test Coverage Enforcement

**✅ DO:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Why?**
- Enforces code quality
- Prevents coverage regression
- Automated quality gate

---

## 🔒 Security Practices

### 1. Dependency Vulnerability Scanning

**✅ DO:**
```bash
# Regular security scans
mvn org.owasp:dependency-check-maven:check

# Fail build on high severity
mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7
```

**Why?**
- Identifies vulnerabilities
- Prevents insecure dependencies
- Security compliance

### 2. Use Trusted Repositories

**✅ DO:**
```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Maven Central</name>
        <url>https://repo.maven.apache.org/maven2</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

**❌ DON'T:**
```xml
<!-- Don't use untrusted repositories -->
<repository>
    <url>http://untrusted-repo.com</url>  <!-- Don't do this -->
</repository>
```

**Why?**
- Prevents malicious dependencies
- Ensures artifact integrity
- Security best practice

### 3. Verify Dependencies

**✅ DO:**
```bash
# Verify dependency checksums
mvn dependency:purge-local-repository

# Check for updates
mvn versions:display-dependency-updates
```

**Why?**
- Ensures dependency integrity
- Identifies outdated dependencies
- Security updates

---

## ⚡ Performance Optimization

### 1. Use Maven Daemon (Optional)

**✅ DO:**
```bash
# Install Maven Daemon (mvnd)
# Faster builds by keeping JVM warm
mvnd clean install
```

**Why?**
- Faster builds
- Reduced startup time
- Better performance

### 2. Optimize Dependency Resolution

**✅ DO:**
```bash
# Use dependency:go-offline
mvn dependency:go-offline

# Then build offline
mvn clean install -o
```

**Why?**
- Faster builds
- Works offline
- Reduces network calls

### 3. Parallel Module Building

**✅ DO:**
```bash
# Build modules in parallel
mvn clean install -T 4

# Or use Maven Daemon
mvnd clean install
```

**Why?**
- Faster multi-module builds
- Better CPU utilization
- Reduced build time

### 4. Incremental Compilation

**✅ DO:**
```bash
# Only compile changed files
mvn compiler:compile

# Skip tests for faster iteration
mvn compile -DskipTests
```

**Why?**
- Faster development cycle
- Quick feedback
- Better developer experience

---

## 📝 Common Commands

### Build Commands

```bash
# Clean and build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run specific profile
mvn clean install -Pdev

# Build with parallel execution
mvn clean install -T 4

# Build offline
mvn clean install -o
```

### Test Commands

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests matching pattern
mvn test -Dtest=*ServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### Dependency Commands

```bash
# List dependency tree
mvn dependency:tree

# Download dependencies
mvn dependency:resolve

# Analyze dependencies
mvn dependency:analyze

# Check for updates
mvn versions:display-dependency-updates

# Update versions
mvn versions:use-latest-versions
```

### Plugin Commands

```bash
# Generate coverage report
mvn jacoco:report

# Check coverage threshold
mvn jacoco:check

# Security scan
mvn org.owasp:dependency-check-maven:check

# Generate site documentation
mvn site
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Dependency Resolution Failed

**Problem:**
```
Could not resolve dependencies
```

**Solution:**
```bash
# Clear local repository
rm -rf ~/.m2/repository/com/nicecommerce

# Force update
mvn clean install -U

# Check network connectivity
mvn dependency:resolve -X
```

#### 2. Out of Memory

**Problem:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution:**
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Or in pom.xml
<properties>
    <maven.compiler.maxmem>2048m</maven.compiler.maxmem>
</properties>
```

#### 3. Plugin Not Found

**Problem:**
```
Plugin not found
```

**Solution:**
```bash
# Update plugin versions
mvn versions:display-plugin-updates

# Clear plugin cache
rm -rf ~/.m2/repository/org/apache/maven/plugins
```

#### 4. Build Fails on Tests

**Problem:**
```
Tests failing
```

**Solution:**
```bash
# Run tests with debug
mvn test -X

# Run specific test
mvn test -Dtest=UserServiceTest

# Skip tests temporarily
mvn clean install -DskipTests
```

#### 5. Slow Builds

**Problem:**
```
Builds are slow
```

**Solution:**
```bash
# Use parallel execution
mvn clean install -T 4

# Use Maven Daemon
mvnd clean install

# Go offline after downloading
mvn dependency:go-offline
mvn clean install -o
```

---

## ✅ Maven Best Practices Checklist

### Project Setup
- [ ] Use Maven standard directory layout
- [ ] Use parent POM (Spring Boot parent)
- [ ] Define project metadata
- [ ] Use properties for versions
- [ ] Configure encoding (UTF-8)

### Dependencies
- [ ] Use appropriate scopes
- [ ] Exclude unwanted transitive dependencies
- [ ] Use BOM for related dependencies
- [ ] Avoid version ranges
- [ ] Check for updates regularly

### Build
- [ ] Use Maven Wrapper
- [ ] Configure compiler plugin
- [ ] Configure test plugins
- [ ] Use build profiles
- [ ] Optimize dependency resolution

### Testing
- [ ] Separate unit and integration tests
- [ ] Use parallel test execution
- [ ] Enforce code coverage
- [ ] Run tests before committing

### Security
- [ ] Scan for vulnerabilities
- [ ] Use trusted repositories
- [ ] Verify dependencies
- [ ] Keep dependencies updated

### Performance
- [ ] Use parallel builds
- [ ] Cache dependencies
- [ ] Optimize plugin configuration
- [ ] Use incremental compilation

---

## 📚 Additional Resources

- [Maven Official Documentation](https://maven.apache.org/guides/)
- [Maven Best Practices](https://maven.apache.org/guides/introduction/introduction-to-best-practices.html)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/)
- [Maven Central Repository](https://search.maven.org/)

---

## 🎯 Quick Reference

### Essential Commands

```bash
# Build
mvn clean install

# Test
mvn test

# Run
mvn spring-boot:run

# Coverage
mvn jacoco:report

# Security
mvn org.owasp:dependency-check-maven:check

# Updates
mvn versions:display-dependency-updates
```

### Essential Properties

```xml
<java.version>17</java.version>
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
```

---

**Follow these best practices to ensure maintainable, secure, and performant Maven builds!** 🚀

