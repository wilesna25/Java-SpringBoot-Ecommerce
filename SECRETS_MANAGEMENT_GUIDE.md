# 🔐 Secrets & Environment Variables Management Guide
## Complete Guide for Handling Secrets in Spring Boot

> **Author**: Senior Java/Spring Boot Expert  
> **Best Practices**: Production-ready secrets management

---

## 📋 Table of Contents

1. [Understanding Property Resolution](#understanding-property-resolution)
2. [Methods to Provide Values](#methods-to-provide-values)
3. [Local Development Setup](#local-development-setup)
4. [Production Setup](#production-setup)
5. [Security Best Practices](#security-best-practices)
6. [Examples](#examples)

---

## 🔍 Understanding Property Resolution

### Syntax: `${VARIABLE_NAME:default_value}`

```yaml
# In application.yml
password: ${DATABASE_PASSWORD:nicecommerce}
```

**How it works:**
1. Spring Boot looks for environment variable `DATABASE_PASSWORD`
2. If found → uses that value
3. If not found → uses default value `nicecommerce`
4. If neither exists → application fails to start (unless marked optional)

### Property Resolution Order

```
┌─────────────────────────────────────────────────────────┐
│         PROPERTY RESOLUTION ORDER                       │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  1. Environment Variables (Highest Priority)             │
│     • System environment variables                       │
│     • Process environment variables                     │
│                                                           │
│  2. System Properties                                    │
│     • -Dproperty=value                                   │
│                                                           │
│  3. application-{profile}.yml                           │
│     • application-prod.yml                               │
│     • application-dev.yml                                │
│                                                           │
│  4. application.yml (Lowest Priority)                    │
│     • Default values                                     │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Methods to Provide Values

### Method 1: Environment Variables (Recommended)

#### Windows (PowerShell)
```powershell
# Set for current session
$env:DATABASE_PASSWORD = "my-secure-password"
$env:DATABASE_USERNAME = "myuser"
$env:DATABASE_URL = "jdbc:mysql://localhost:3306/nicecommerce"

# Set permanently (User level)
[System.Environment]::SetEnvironmentVariable("DATABASE_PASSWORD", "my-secure-password", "User")

# Set permanently (System level - requires admin)
[System.Environment]::SetEnvironmentVariable("DATABASE_PASSWORD", "my-secure-password", "Machine")
```

#### Windows (Command Prompt)
```cmd
# Set for current session
set DATABASE_PASSWORD=my-secure-password
set DATABASE_USERNAME=myuser

# Set permanently
setx DATABASE_PASSWORD "my-secure-password"
setx DATABASE_USERNAME "myuser"
```

#### Linux/Mac (Bash)
```bash
# Set for current session
export DATABASE_PASSWORD="my-secure-password"
export DATABASE_USERNAME="myuser"

# Set permanently (add to ~/.bashrc or ~/.zshrc)
echo 'export DATABASE_PASSWORD="my-secure-password"' >> ~/.bashrc
source ~/.bashrc
```

### Method 2: .env File (Local Development)

Create `.env` file in project root:

```bash
# .env file
DATABASE_URL=jdbc:mysql://localhost:3306/nicecommerce
DATABASE_USERNAME=nicecommerce
DATABASE_PASSWORD=my-secure-password
DATABASE_HOST=localhost
DATABASE_PORT=3306

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json

# JWT
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters-long

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP_ID=nicecommerce-group

# Application
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
```

**Load .env file:**

#### Option A: Manual Loading (PowerShell)
```powershell
# Load .env file
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]+)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
    }
}
```

#### Option B: Use dotenv-java Library
Add to `pom.xml`:
```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Method 3: System Properties

```powershell
# Run with system properties
mvn spring-boot:run -Dspring-boot.run.arguments="--DATABASE_PASSWORD=my-password"

# Or in Java
java -jar app.jar --DATABASE_PASSWORD=my-password
```

### Method 4: Application Properties Files

Create `application-local.yml`:

```yaml
spring:
  datasource:
    password: my-local-password
    username: my-local-user
```

Run with profile:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Method 5: IDE Configuration (IntelliJ IDEA)

1. **Run Configuration**
   - Go to Run → Edit Configurations
   - Select your Spring Boot configuration
   - Add environment variables:
     ```
     DATABASE_PASSWORD=my-password
     DATABASE_USERNAME=myuser
     ```

2. **Environment Variables**
   - Add in "Environment variables" section
   - Or use "Environment file" to load from `.env`

---

## 💻 Local Development Setup

### Step 1: Create .env File

Create `.env` in project root:

```bash
# Database
DATABASE_URL=jdbc:mysql://localhost:3306/nicecommerce
DATABASE_USERNAME=nicecommerce
DATABASE_PASSWORD=your-local-password
DATABASE_HOST=localhost
DATABASE_PORT=3306

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP_ID=nicecommerce-group

# Firebase (optional for local)
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json

# JWT
JWT_SECRET=local-dev-secret-key-minimum-32-characters-long

# Application
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
JPA_SHOW_SQL=true
```

### Step 2: Add .env to .gitignore

```gitignore
# .gitignore
.env
.env.local
.env.*.local
*.env
```

### Step 3: Create .env.example Template

```bash
# .env.example (commit this to git)
DATABASE_URL=jdbc:mysql://localhost:3306/nicecommerce
DATABASE_USERNAME=nicecommerce
DATABASE_PASSWORD=CHANGE_THIS
DATABASE_HOST=localhost
DATABASE_PORT=3306

KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP_ID=nicecommerce-group

FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json

JWT_SECRET=CHANGE_THIS_MINIMUM_32_CHARACTERS

SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
```

### Step 4: Load .env in PowerShell Script

Create `load-env.ps1`:

```powershell
# load-env.ps1
if (Test-Path .env) {
    Get-Content .env | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
            Write-Host "Loaded: $name"
        }
    }
    Write-Host "Environment variables loaded from .env"
} else {
    Write-Host ".env file not found"
}
```

Usage:
```powershell
. .\load-env.ps1
mvn spring-boot:run
```

---

## 🚀 Production Setup

### Method 1: GCP Secret Manager (Recommended)

#### Create Secrets
```bash
# Create database password secret
echo -n "your-secure-password" | \
    gcloud secrets create db-password \
    --data-file=-

# Create JWT secret
echo -n "your-256-bit-secret" | \
    gcloud secrets create jwt-secret \
    --data-file=-
```

#### Use in Cloud Run
```bash
gcloud run deploy nicecommerce \
    --set-secrets="DATABASE_PASSWORD=db-password:latest" \
    --set-secrets="JWT_SECRET=jwt-secret:latest"
```

### Method 2: Environment Variables in Cloud Run

```bash
gcloud run deploy nicecommerce \
    --set-env-vars="DATABASE_URL=jdbc:mysql://..." \
    --set-env-vars="DATABASE_USERNAME=nicecommerce"
```

**⚠️ Note**: Don't use `--set-env-vars` for secrets! Use `--set-secrets` instead.

### Method 3: Kubernetes Secrets

```yaml
# k8s-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: nicecommerce-secrets
type: Opaque
stringData:
  DATABASE_PASSWORD: your-password
  JWT_SECRET: your-secret
```

---

## 🔒 Security Best Practices

### ✅ DO

1. **Never commit secrets to Git**
   ```gitignore
   .env
   *.env
   application-local.yml
   firebase-service-account.json
   ```

2. **Use Secret Manager in Production**
   - GCP Secret Manager
   - AWS Secrets Manager
   - Azure Key Vault

3. **Use Strong Defaults for Development Only**
   ```yaml
   # OK for local dev
   password: ${DATABASE_PASSWORD:dev-password}
   
   # BAD for production
   password: ${DATABASE_PASSWORD:production-password}
   ```

4. **Rotate Secrets Regularly**
   - Change passwords every 90 days
   - Rotate JWT secrets
   - Update API keys

5. **Use Different Secrets per Environment**
   - Dev: `db-password-dev`
   - Staging: `db-password-staging`
   - Prod: `db-password-prod`

### ❌ DON'T

1. **Don't hardcode secrets**
   ```yaml
   # BAD
   password: my-secret-password
   
   # GOOD
   password: ${DATABASE_PASSWORD:}
   ```

2. **Don't commit .env files**
   ```gitignore
   .env
   .env.local
   .env.*.local
   ```

3. **Don't log secrets**
   ```java
   // BAD
   log.info("Password: {}", password);
   
   // GOOD
   log.info("Database connection established");
   ```

4. **Don't use default values in production**
   ```yaml
   # BAD for production
   password: ${DATABASE_PASSWORD:nicecommerce}
   
   # GOOD - fail if not set
   password: ${DATABASE_PASSWORD:}
   ```

---

## 📝 Examples

### Example 1: Local Development

**1. Create `.env` file:**
```bash
DATABASE_PASSWORD=local-dev-password
DATABASE_USERNAME=nicecommerce
JWT_SECRET=local-dev-secret-key-32-chars-minimum
```

**2. Load and run:**
```powershell
# PowerShell
. .\load-env.ps1
mvn spring-boot:run
```

### Example 2: Docker Compose

**docker-compose.yml:**
```yaml
services:
  app:
    image: nicecommerce:latest
    environment:
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    env_file:
      - .env
```

**Run:**
```powershell
docker-compose up
```

### Example 3: Cloud Run Deployment

```bash
# Deploy with secrets
gcloud run deploy nicecommerce \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-secrets="DATABASE_PASSWORD=db-password:latest" \
    --set-secrets="JWT_SECRET=jwt-secret:latest"
```

### Example 4: Required vs Optional Variables

```yaml
# Required - fails if not set
password: ${DATABASE_PASSWORD:}

# Optional - uses default if not set
password: ${DATABASE_PASSWORD:nicecommerce}

# Optional with empty default
host: ${DATABASE_HOST:localhost}
```

---

## 🔍 Verification

### Check Environment Variables

#### PowerShell
```powershell
# List all environment variables
Get-ChildItem Env:

# Check specific variable
$env:DATABASE_PASSWORD

# Or
[System.Environment]::GetEnvironmentVariable("DATABASE_PASSWORD")
```

#### Command Prompt
```cmd
# List all
set

# Check specific
echo %DATABASE_PASSWORD%
```

#### Linux/Mac
```bash
# List all
env

# Check specific
echo $DATABASE_PASSWORD
```

### Test Application Startup

```powershell
# Run with verbose logging
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"

# Check what values are being used
# Look for: "The following profiles are active"
# Look for: "DataSource configured"
```

---

## 📚 Quick Reference

### Common Environment Variables

```bash
# Database
DATABASE_URL=jdbc:mysql://localhost:3306/nicecommerce
DATABASE_USERNAME=nicecommerce
DATABASE_PASSWORD=your-password
DATABASE_HOST=localhost
DATABASE_PORT=3306

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP_ID=nicecommerce-group

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CREDENTIALS_PATH=./firebase-service-account.json

# JWT
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters

# Application
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
JPA_SHOW_SQL=false
```

### Property Resolution Syntax

```yaml
# With default value
${VARIABLE_NAME:default_value}

# Required (no default)
${VARIABLE_NAME:}

# Nested resolution
${OUTER_VAR:${INNER_VAR:default}}
```

---

## ✅ Checklist

- [ ] `.env` file created (not committed)
- [ ] `.env.example` template created (committed)
- [ ] `.env` added to `.gitignore`
- [ ] Environment variables documented
- [ ] Secrets use Secret Manager in production
- [ ] No hardcoded secrets in code
- [ ] Different secrets per environment
- [ ] Secrets rotation plan in place

---

**Last Updated**: 2024  
**Status**: ✅ Production Ready  
**Security**: Follows best practices

