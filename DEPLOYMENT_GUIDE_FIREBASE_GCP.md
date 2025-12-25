# Complete Deployment Guide
## NiceCommerce Spring Boot on Firebase & Google Cloud Platform

This guide provides step-by-step instructions for deploying the NiceCommerce Spring Boot application to Google Cloud Platform with Firebase Authentication, following security best practices and using Google Cloud Secret Manager.

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Firebase Setup](#firebase-setup)
3. [Google Cloud Platform Setup](#google-cloud-platform-setup)
4. [Secrets Management](#secrets-management)
5. [Database Setup](#database-setup)
6. [Cloud Storage Setup](#cloud-storage-setup)
7. [Application Configuration](#application-configuration)
8. [Build and Deploy](#build-and-deploy)
9. [CI/CD Pipeline](#cicd-pipeline)
10. [Monitoring and Logging](#monitoring-and-logging)
11. [Security Best Practices](#security-best-practices)
12. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Accounts and Tools

1. **Google Cloud Platform Account**
   - Active GCP account with billing enabled
   - Project created (or create one)

2. **Firebase Account**
   - Linked to your GCP project

3. **Local Tools**
   ```bash
   # Install Google Cloud SDK
   # macOS
   brew install google-cloud-sdk
   
   # Linux
   curl https://sdk.cloud.google.com | bash
   
   # Windows - Download from: https://cloud.google.com/sdk/docs/install
   
   # Verify installation
   gcloud --version
   ```

4. **Authentication**
   ```bash
   # Login to GCP
   gcloud auth login
   
   # Set default project
   gcloud config set project YOUR_PROJECT_ID
   
   # Enable Application Default Credentials
   gcloud auth application-default login
   ```

---

## 🔥 Firebase Setup

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or select existing project
3. Enter project name: `nicecommerce` (or your preferred name)
4. Enable Google Analytics (optional but recommended)
5. Click **"Create project"**

### Step 2: Enable Authentication

1. In Firebase Console, navigate to **Authentication**
2. Click **"Get Started"**
3. Enable **Email/Password** provider:
   - Click on "Email/Password"
   - Toggle "Enable"
   - Click "Save"

4. Enable **Google** provider (optional, for Google Sign-In):
   - Click on "Google"
   - Toggle "Enable"
   - Add support email
   - Click "Save"

### Step 3: Generate Service Account Key

1. Go to **Project Settings** → **Service Accounts**
2. Click **"Generate New Private Key"**
3. Click **"Generate Key"** (JSON file downloads)
4. **⚠️ IMPORTANT**: Save this file securely - you'll need it for Secret Manager

### Step 4: Configure Firebase Web App (for client-side)

1. In Firebase Console, go to **Project Settings** → **General**
2. Scroll to **"Your apps"** section
3. Click **Web icon** (`</>`) to add web app
4. Register app name: `nicecommerce-web`
5. Copy the Firebase configuration:
   ```javascript
   const firebaseConfig = {
     apiKey: "YOUR_API_KEY",
     authDomain: "YOUR_PROJECT_ID.firebaseapp.com",
     projectId: "YOUR_PROJECT_ID",
     storageBucket: "YOUR_PROJECT_ID.appspot.com",
     messagingSenderId: "YOUR_SENDER_ID",
     appId: "YOUR_APP_ID"
   };
   ```
6. Save this configuration for client-side integration

---

## ☁️ Google Cloud Platform Setup

### Step 1: Create GCP Project

```bash
# Create new project (or use existing)
gcloud projects create nicecommerce-prod \
    --name="NiceCommerce Production"

# Set as default project
gcloud config set project nicecommerce-prod

# Get project ID
PROJECT_ID=$(gcloud config get-value project)
echo "Project ID: $PROJECT_ID"
```

### Step 2: Enable Required APIs

```bash
# Enable all required APIs
gcloud services enable \
    cloudbuild.googleapis.com \
    run.googleapis.com \
    sqladmin.googleapis.com \
    storage-component.googleapis.com \
    secretmanager.googleapis.com \
    logging.googleapis.com \
    monitoring.googleapis.com \
    artifactregistry.googleapis.com

# Verify APIs are enabled
gcloud services list --enabled
```

### Step 3: Set Up Billing

1. Go to [GCP Console Billing](https://console.cloud.google.com/billing)
2. Link a billing account to your project
3. Verify billing is active

---

## 🔐 Secrets Management

### Step 1: Create Secret Manager Secrets

```bash
# Set project ID variable
export PROJECT_ID=$(gcloud config get-value project)

# 1. Firebase Service Account Key
# First, encode the JSON file to base64 (optional, or store as-is)
gcloud secrets create firebase-service-account \
    --data-file=path/to/firebase-service-account.json \
    --replication-policy="automatic"

# Or create from file directly
gcloud secrets create firebase-service-account \
    --data-file=firebase-service-account.json

# 2. Database Password
echo -n "your-secure-db-password" | \
    gcloud secrets create db-password \
    --data-file=-

# 3. JWT Secret (if still using JWT for some operations)
echo -n "your-256-bit-secret-key-minimum-32-characters-long" | \
    gcloud secrets create jwt-secret \
    --data-file=-

# 4. MercadoPago Access Token
echo -n "your-mercadopago-access-token" | \
    gcloud secrets create mercadopago-access-token \
    --data-file=-

# 5. MercadoPago Webhook Secret
echo -n "your-mercadopago-webhook-secret" | \
    gcloud secrets create mercadopago-webhook-secret \
    --data-file=-

# 6. Email SMTP Password (if using SMTP)
echo -n "your-smtp-password" | \
    gcloud secrets create smtp-password \
    --data-file=-

# 7. CORS Allowed Origins (comma-separated)
echo -n "https://yourdomain.com,https://www.yourdomain.com" | \
    gcloud secrets create cors-allowed-origins \
    --data-file=-
```

### Step 2: Grant Secret Access to Cloud Run

```bash
# Get Cloud Run service account email
SERVICE_ACCOUNT=$(gcloud iam service-accounts list \
    --filter="displayName:Cloud Run" \
    --format="value(email)")

# Or use the default compute service account
SERVICE_ACCOUNT="${PROJECT_NUMBER}-compute@developer.gserviceaccount.com"

# Grant secret accessor role
gcloud secrets add-iam-policy-binding firebase-service-account \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding db-password \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding jwt-secret \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding mercadopago-access-token \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding mercadopago-webhook-secret \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding smtp-password \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding cors-allowed-origins \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"
```

### Step 3: Verify Secrets

```bash
# List all secrets
gcloud secrets list

# View secret metadata (not the value)
gcloud secrets describe firebase-service-account

# Test accessing secret (requires proper permissions)
gcloud secrets versions access latest --secret="firebase-service-account"
```

---

## 🗄️ Database Setup

### Step 1: Create Cloud SQL Instance

```bash
# Create MySQL instance
gcloud sql instances create nicecommerce-db \
    --database-version=MYSQL_8_0 \
    --tier=db-f1-micro \
    --region=us-central1 \
    --root-password=$(gcloud secrets versions access latest --secret="db-password") \
    --storage-type=SSD \
    --storage-size=20GB \
    --storage-auto-increase \
    --backup-start-time=03:00 \
    --enable-bin-log \
    --maintenance-window-day=SUN \
    --maintenance-window-hour=4 \
    --maintenance-release-channel=production \
    --deletion-protection

# Note: For production, use higher tier (e.g., db-n1-standard-1)
```

### Step 2: Create Database

```bash
# Create database
gcloud sql databases create nicecommerce \
    --instance=nicecommerce-db \
    --charset=utf8mb4 \
    --collation=utf8mb4_unicode_ci
```

### Step 3: Create Database User

```bash
# Create database user
DB_PASSWORD=$(gcloud secrets versions access latest --secret="db-password")

gcloud sql users create nicecommerce-user \
    --instance=nicecommerce-db \
    --password="${DB_PASSWORD}"
```

### Step 4: Configure Database Access

```bash
# Get instance connection name
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe nicecommerce-db \
    --format="value(connectionName)")

echo "Instance connection name: ${INSTANCE_CONNECTION_NAME}"

# Allow Cloud Run to connect (automatic with Cloud SQL Proxy)
# No additional configuration needed for Cloud Run
```

### Step 5: Run Database Migrations

```bash
# Option 1: Using Cloud SQL Proxy locally
# Download Cloud SQL Proxy
curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.6.0/cloud-sql-proxy.darwin.arm64
chmod +x cloud-sql-proxy

# Start proxy in background
./cloud-sql-proxy ${INSTANCE_CONNECTION_NAME} --port=3306 &

# Run migrations (if using Flyway/Liquibase)
# Or let JPA create schema (development only)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.jpa.hibernate.ddl-auto=update"

# Option 2: Using Cloud Shell
gcloud sql connect nicecommerce-db --user=nicecommerce-user
# Then run SQL scripts manually
```

---

## 📦 Cloud Storage Setup

### Step 1: Create Storage Bucket

```bash
# Create bucket for media files
gsutil mb -p ${PROJECT_ID} -c STANDARD -l us-central1 \
    gs://${PROJECT_ID}-nicecommerce-media

# Set bucket permissions
gsutil iam ch allUsers:objectViewer \
    gs://${PROJECT_ID}-nicecommerce-media

# Enable versioning (optional)
gsutil versioning set on gs://${PROJECT_ID}-nicecommerce-media

# Set lifecycle policy (delete old versions after 30 days)
cat > lifecycle.json <<EOF
{
  "lifecycle": {
    "rule": [
      {
        "action": {"type": "Delete"},
        "condition": {
          "age": 30,
          "numNewerVersions": 1
        }
      }
    ]
  }
}
EOF

gsutil lifecycle set lifecycle.json gs://${PROJECT_ID}-nicecommerce-media
```

### Step 2: Configure CORS (if needed)

```bash
# Create CORS configuration
cat > cors.json <<EOF
[
  {
    "origin": ["https://yourdomain.com"],
    "method": ["GET", "HEAD", "PUT", "POST", "DELETE"],
    "responseHeader": ["Content-Type", "Authorization"],
    "maxAgeSeconds": 3600
  }
]
EOF

gsutil cors set cors.json gs://${PROJECT_ID}-nicecommerce-media
```

---

## ⚙️ Application Configuration

### Step 1: Update application-prod.yml

Create or update `src/main/resources/application-prod.yml`:

```yaml
spring:
  application:
    name: nicecommerce-springboot
  
  # Cloud SQL Configuration
  datasource:
    url: jdbc:mysql:///${DATABASE_NAME:nicecommerce}?cloudSqlInstance=${GCP_CLOUD_SQL_INSTANCE}&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=true
    username: ${DATABASE_USERNAME:nicecommerce-user}
    password: ${DATABASE_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate  # Never auto-update in production
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: false

# Server Configuration
server:
  port: 8080
  compression:
    enabled: true

# Application Configuration
app:
  name: NiceCommerce
  version: 1.0.0
  
  # CORS
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS}
  
  # Storage
  storage:
    type: gcp
    gcp:
      bucket-name: ${GCP_STORAGE_BUCKET}
      project-id: ${GCP_PROJECT_ID}
  
  # MercadoPago
  mercadopago:
    access-token: ${MERCADOPAGO_ACCESS_TOKEN}
    webhook-secret: ${MERCADOPAGO_WEBHOOK_SECRET}
  
  # Email
  mail:
    enabled: ${MAIL_ENABLED:true}
    from: ${MAIL_FROM:noreply@nicecommerce.com}
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

# Firebase Configuration
firebase:
  credentials:
    path: ${FIREBASE_CREDENTIALS_PATH:}
    json: ${FIREBASE_CREDENTIALS_JSON:}
  project-id: ${FIREBASE_PROJECT_ID}

# Google Cloud Platform Configuration
gcp:
  project-id: ${GCP_PROJECT_ID}
  cloud-sql:
    instance-connection-name: ${GCP_CLOUD_SQL_INSTANCE}
    database-name: ${DATABASE_NAME:nicecommerce}
  secret-manager:
    enabled: true
    secret-prefix: nicecommerce

# Logging
logging:
  level:
    root: INFO
    com.nicecommerce: INFO
    org.springframework: WARN
    org.hibernate: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

### Step 2: Create Secret Manager Configuration Class

Create `src/main/java/com/nicecommerce/core/config/SecretManagerConfig.java`:

```java
package com.nicecommerce.core.config;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("prod")
@Slf4j
public class SecretManagerConfig {

    @Value("${gcp.project-id}")
    private String projectId;

    @Bean
    public SecretManagerServiceClient secretManagerClient() throws IOException {
        log.info("Initializing Secret Manager client for project: {}", projectId);
        return SecretManagerServiceClient.create();
    }
}
```

### Step 3: Create Secret Manager Service

Create `src/main/java/com/nicecommerce/core/service/SecretManagerService.java`:

```java
package com.nicecommerce.core.service;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecretManagerService {

    private final SecretManagerServiceClient secretManagerClient;

    @Value("${gcp.project-id}")
    private String projectId;

    /**
     * Get secret value from Secret Manager
     */
    public String getSecret(String secretName) {
        try {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, "latest");
            byte[] response = secretManagerClient.accessSecretVersion(secretVersionName).getPayload().getData().toByteArray();
            return new String(response, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error accessing secret {}: {}", secretName, e.getMessage());
            throw new RuntimeException("Failed to access secret: " + secretName, e);
        }
    }
}
```

---

## 🚀 Build and Deploy

### Step 1: Build Docker Image

Create `Dockerfile` (if not exists):

```dockerfile
# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR
COPY --from=build /app/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 2: Create Artifact Registry Repository

```bash
# Create repository for Docker images
gcloud artifacts repositories create nicecommerce-repo \
    --repository-format=docker \
    --location=us-central1 \
    --description="NiceCommerce Docker images"

# Configure Docker to use Artifact Registry
gcloud auth configure-docker us-central1-docker.pkg.dev
```

### Step 3: Build and Push Image

```bash
# Set variables
export PROJECT_ID=$(gcloud config get-value project)
export REGION=us-central1
export REPO=nicecommerce-repo
export IMAGE=nicecommerce
export TAG=latest

# Build image
docker build -t ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:${TAG} .

# Push image
docker push ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:${TAG}
```

### Step 4: Deploy to Cloud Run

```bash
# Get instance connection name
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe nicecommerce-db \
    --format="value(connectionName)")

# Get secrets
DB_PASSWORD_SECRET="projects/${PROJECT_ID}/secrets/db-password/versions/latest"
FIREBASE_CREDENTIALS_SECRET="projects/${PROJECT_ID}/secrets/firebase-service-account/versions/latest"
JWT_SECRET="projects/${PROJECT_ID}/secrets/jwt-secret/versions/latest"
MERCADOPAGO_TOKEN_SECRET="projects/${PROJECT_ID}/secrets/mercadopago-access-token/versions/latest"
MERCADOPAGO_WEBHOOK_SECRET="projects/${PROJECT_ID}/secrets/mercadopago-webhook-secret/versions/latest"
SMTP_PASSWORD_SECRET="projects/${PROJECT_ID}/secrets/smtp-password/versions/latest"
CORS_ORIGINS_SECRET="projects/${PROJECT_ID}/secrets/cors-allowed-origins/versions/latest"

# Deploy to Cloud Run
gcloud run deploy nicecommerce \
    --image ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:${TAG} \
    --platform managed \
    --region ${REGION} \
    --allow-unauthenticated \
    --service-account ${PROJECT_ID}@appspot.gserviceaccount.com \
    --add-cloudsql-instances ${INSTANCE_CONNECTION_NAME} \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars="GCP_PROJECT_ID=${PROJECT_ID}" \
    --set-env-vars="GCP_CLOUD_SQL_INSTANCE=${INSTANCE_CONNECTION_NAME}" \
    --set-env-vars="DATABASE_NAME=nicecommerce" \
    --set-env-vars="DATABASE_USERNAME=nicecommerce-user" \
    --set-env-vars="GCP_STORAGE_BUCKET=${PROJECT_ID}-nicecommerce-media" \
    --set-env-vars="FIREBASE_PROJECT_ID=${PROJECT_ID}" \
    --set-secrets="DATABASE_PASSWORD=${DB_PASSWORD_SECRET}" \
    --set-secrets="FIREBASE_CREDENTIALS_PATH=${FIREBASE_CREDENTIALS_SECRET}" \
    --set-secrets="JWT_SECRET=${JWT_SECRET}" \
    --set-secrets="MERCADOPAGO_ACCESS_TOKEN=${MERCADOPAGO_TOKEN_SECRET}" \
    --set-secrets="MERCADOPAGO_WEBHOOK_SECRET=${MERCADOPAGO_WEBHOOK_SECRET}" \
    --set-secrets="MAIL_PASSWORD=${SMTP_PASSWORD_SECRET}" \
    --set-secrets="CORS_ALLOWED_ORIGINS=${CORS_ORIGINS_SECRET}" \
    --memory=512Mi \
    --cpu=1 \
    --timeout=300 \
    --max-instances=10 \
    --min-instances=0 \
    --concurrency=80 \
    --port=8080

# Get service URL
SERVICE_URL=$(gcloud run services describe nicecommerce \
    --region ${REGION} \
    --format="value(status.url)")

echo "Service deployed at: ${SERVICE_URL}"
```

### Step 5: Update Firebase Allowed Domains

1. Go to Firebase Console → Authentication → Settings
2. Add your Cloud Run domain to **Authorized domains**
3. Add your custom domain if you have one

---

## 🔄 CI/CD Pipeline

### Step 1: Create Cloud Build Configuration

Create `cloudbuild.yaml`:

```yaml
steps:
  # Build the application
  - name: 'maven:3.9-eclipse-temurin-17'
    entrypoint: 'mvn'
    args: ['clean', 'package', '-DskipTests']
    dir: 'nicecommerce-springboot'

  # Build Docker image
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'build'
      - '-t'
      - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:${SHORT_SHA}'
      - '-t'
      - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:latest'
      - '.'
    dir: 'nicecommerce-springboot'

  # Push Docker image
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:${SHORT_SHA}'
  
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:latest'

  # Deploy to Cloud Run
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: gcloud
    args:
      - 'run'
      - 'deploy'
      - '${_SERVICE_NAME}'
      - '--image'
      - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:${SHORT_SHA}'
      - '--region'
      - '${_REGION}'
      - '--platform'
      - 'managed'
      - '--allow-unauthenticated'
      - '--add-cloudsql-instances'
      - '${_INSTANCE_CONNECTION_NAME}'
      - '--set-env-vars'
      - 'SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=${PROJECT_ID},GCP_CLOUD_SQL_INSTANCE=${_INSTANCE_CONNECTION_NAME},DATABASE_NAME=nicecommerce,DATABASE_USERNAME=nicecommerce-user,GCP_STORAGE_BUCKET=${PROJECT_ID}-nicecommerce-media,FIREBASE_PROJECT_ID=${PROJECT_ID}'
      - '--set-secrets'
      - 'DATABASE_PASSWORD=projects/${PROJECT_ID}/secrets/db-password/versions/latest,FIREBASE_CREDENTIALS_PATH=projects/${PROJECT_ID}/secrets/firebase-service-account/versions/latest,JWT_SECRET=projects/${PROJECT_ID}/secrets/jwt-secret/versions/latest,MERCADOPAGO_ACCESS_TOKEN=projects/${PROJECT_ID}/secrets/mercadopago-access-token/versions/latest,MERCADOPAGO_WEBHOOK_SECRET=projects/${PROJECT_ID}/secrets/mercadopago-webhook-secret/versions/latest,MAIL_PASSWORD=projects/${PROJECT_ID}/secrets/smtp-password/versions/latest,CORS_ALLOWED_ORIGINS=projects/${PROJECT_ID}/secrets/cors-allowed-origins/versions/latest'
      - '--memory'
      - '512Mi'
      - '--cpu'
      - '1'
      - '--timeout'
      - '300'
      - '--max-instances'
      - '10'

images:
  - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:${SHORT_SHA}'
  - '${_REGION}-docker.pkg.dev/${PROJECT_ID}/${_REPO}/${_IMAGE}:latest'

options:
  machineType: 'N1_HIGHCPU_8'
  logging: CLOUD_LOGGING_ONLY

substitutions:
  _REGION: 'us-central1'
  _REPO: 'nicecommerce-repo'
  _IMAGE: 'nicecommerce'
  _SERVICE_NAME: 'nicecommerce'
  _INSTANCE_CONNECTION_NAME: 'YOUR_PROJECT_ID:us-central1:nicecommerce-db'

timeout: '1200s'
```

### Step 2: Set Up Cloud Build Triggers

```bash
# Create trigger for main branch
gcloud builds triggers create github \
    --name="nicecommerce-deploy" \
    --repo-name="YOUR_REPO_NAME" \
    --repo-owner="YOUR_GITHUB_USERNAME" \
    --branch-pattern="^main$" \
    --build-config="cloudbuild.yaml" \
    --substitutions="_INSTANCE_CONNECTION_NAME=${PROJECT_ID}:us-central1:nicecommerce-db"

# Or use Cloud Source Repositories
gcloud source repos create nicecommerce
gcloud builds triggers create cloud-source-repositories \
    --name="nicecommerce-deploy" \
    --repo="nicecommerce" \
    --branch-pattern="^main$" \
    --build-config="cloudbuild.yaml"
```

### Step 3: Manual Build Trigger

```bash
# Submit build manually
gcloud builds submit --config cloudbuild.yaml \
    --substitutions="_INSTANCE_CONNECTION_NAME=${PROJECT_ID}:us-central1:nicecommerce-db"
```

---

## 📊 Monitoring and Logging

### Step 1: Enable Logging

Logs are automatically sent to Cloud Logging. View them:

```bash
# View recent logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce" \
    --limit 50 \
    --format json

# View error logs only
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce AND severity>=ERROR" \
    --limit 50
```

### Step 2: Set Up Alerts

```bash
# Create alert policy for high error rate
gcloud alpha monitoring policies create \
    --notification-channels=YOUR_NOTIFICATION_CHANNEL \
    --display-name="High Error Rate" \
    --condition-display-name="Error rate > 5%" \
    --condition-threshold-value=5 \
    --condition-threshold-duration=300s
```

### Step 3: Set Up Uptime Checks

1. Go to [GCP Console → Monitoring → Uptime Checks](https://console.cloud.google.com/monitoring/uptime)
2. Click **"Create Uptime Check"**
3. Configure:
   - Name: `nicecommerce-health-check`
   - Resource type: `URL`
   - URL: `https://your-service-url/actuator/health`
   - Check interval: `1 minute`
4. Create alert policy for downtime

---

## 🔒 Security Best Practices

### 1. IAM Roles and Permissions

```bash
# Create custom service account for Cloud Run
gcloud iam service-accounts create nicecommerce-sa \
    --display-name="NiceCommerce Service Account"

# Grant minimal required permissions
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:nicecommerce-sa@${PROJECT_ID}.iam.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:nicecommerce-sa@${PROJECT_ID}.iam.gserviceaccount.com" \
    --role="roles/cloudsql.client"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:nicecommerce-sa@${PROJECT_ID}.iam.gserviceaccount.com" \
    --role="roles/storage.objectAdmin"

# Update Cloud Run to use custom service account
gcloud run services update nicecommerce \
    --service-account=nicecommerce-sa@${PROJECT_ID}.iam.gserviceaccount.com \
    --region=us-central1
```

### 2. Enable VPC Connector (Optional)

```bash
# Create VPC connector for private Cloud SQL access
gcloud compute networks vpc-access connectors create nicecommerce-connector \
    --region=us-central1 \
    --subnet=default \
    --subnet-project=${PROJECT_ID} \
    --min-instances=2 \
    --max-instances=3

# Update Cloud Run to use VPC connector
gcloud run services update nicecommerce \
    --vpc-connector=nicecommerce-connector \
    --region=us-central1
```

### 3. Enable Cloud Armor (DDoS Protection)

```bash
# Create security policy
gcloud compute security-policies create nicecommerce-policy \
    --description="Security policy for NiceCommerce"

# Add rate limiting rule
gcloud compute security-policies rules create 1000 \
    --security-policy=nicecommerce-policy \
    --expression="origin.region_code == 'US'" \
    --action=deny-403 \
    --preview

# Attach to Cloud Run (requires Load Balancer setup)
```

### 4. Enable Binary Authorization

```bash
# Enable Binary Authorization
gcloud container binauthz policy import policy.yaml
```

### 5. Regular Security Updates

```bash
# Scan container images for vulnerabilities
gcloud container images scan ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:latest

# Update secrets regularly
gcloud secrets versions add db-password --data-file=-
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Cloud SQL Connection Failed

```bash
# Check instance status
gcloud sql instances describe nicecommerce-db

# Test connection
gcloud sql connect nicecommerce-db --user=nicecommerce-user

# Check Cloud Run logs
gcloud logging read "resource.type=cloud_run_revision AND textPayload=~'database'" --limit 10
```

#### 2. Secret Access Denied

```bash
# Verify service account has access
gcloud secrets get-iam-policy firebase-service-account

# Grant access
gcloud secrets add-iam-policy-binding firebase-service-account \
    --member="serviceAccount:nicecommerce-sa@${PROJECT_ID}.iam.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
```

#### 3. Firebase Initialization Failed

```bash
# Check Firebase credentials secret
gcloud secrets versions access latest --secret="firebase-service-account" | jq .

# Verify Firebase project ID
echo $FIREBASE_PROJECT_ID

# Check Cloud Run environment variables
gcloud run services describe nicecommerce --region=us-central1 --format="yaml(spec.template.spec.containers[0].env)"
```

#### 4. High Memory Usage

```bash
# Increase memory
gcloud run services update nicecommerce \
    --memory=1Gi \
    --region=us-central1
```

#### 5. Slow Response Times

```bash
# Increase CPU
gcloud run services update nicecommerce \
    --cpu=2 \
    --region=us-central1

# Check database performance
gcloud sql instances describe nicecommerce-db --format="yaml(settings.tier)"
```

---

## ✅ Verification Checklist

After deployment, verify:

- [ ] Service is running: `gcloud run services list`
- [ ] Health endpoint works: `curl https://your-service-url/actuator/health`
- [ ] Database connection works
- [ ] Firebase authentication works
- [ ] Secrets are accessible
- [ ] Logs are being collected
- [ ] Monitoring is set up
- [ ] Alerts are configured
- [ ] CI/CD pipeline works
- [ ] Security policies are applied

---

## 📚 Additional Resources

- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [Cloud SQL Documentation](https://cloud.google.com/sql/docs)
- [Secret Manager Documentation](https://cloud.google.com/secret-manager/docs)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
- [Cloud Build Documentation](https://cloud.google.com/build/docs)

---

## 🎯 Quick Reference Commands

```bash
# View service logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce" --limit 50

# Update service
gcloud run services update nicecommerce --region=us-central1

# View service details
gcloud run services describe nicecommerce --region=us-central1

# Access secret
gcloud secrets versions access latest --secret="secret-name"

# Connect to database
gcloud sql connect nicecommerce-db --user=nicecommerce-user

# List all secrets
gcloud secrets list

# View Cloud Run metrics
gcloud monitoring time-series list --filter='resource.type="cloud_run_revision"'
```

---

**Last Updated**: After Firebase & GCP deployment implementation  
**Status**: ✅ Production Ready

