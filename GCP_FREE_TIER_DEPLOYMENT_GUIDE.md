# ☁️ Google Cloud Platform Free Tier Deployment Guide
## Complete Step-by-Step Guide with Diagrams

> **Author**: Senior Java/Spring Boot Expert (15+ years experience)  
> **Target**: Deploy NiceCommerce on GCP Free Tier  
> **Estimated Cost**: $0/month (within free tier limits)

---

## 📋 Table of Contents

1. [Free Tier Overview](#free-tier-overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Prerequisites](#prerequisites)
4. [Step 1: GCP Project Setup](#step-1-gcp-project-setup)
5. [Step 2: Enable Free Tier Services](#step-2-enable-free-tier-services)
6. [Step 3: Cloud SQL Setup (Free Tier)](#step-3-cloud-sql-setup-free-tier)
7. [Step 4: Cloud Storage Setup](#step-4-cloud-storage-setup)
8. [Step 5: Secret Manager Setup](#step-5-secret-manager-setup)
9. [Step 6: Cloud Run Deployment](#step-6-cloud-run-deployment)
10. [Step 7: Firebase Integration](#step-7-firebase-integration)
11. [Step 8: CI/CD with Cloud Build](#step-8-cicd-with-cloud-build)
12. [Cost Optimization](#cost-optimization)
13. [Monitoring & Alerts](#monitoring--alerts)
14. [Troubleshooting](#troubleshooting)

---

## 🎁 Free Tier Overview

### GCP Free Tier Services (Always Free)

```
┌─────────────────────────────────────────────────────────┐
│         GCP FREE TIER SERVICES                          │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ✅ Cloud Run                                            │
│     • 2 million requests/month                           │
│     • 360,000 GB-seconds memory                          │
│     • 180,000 vCPU-seconds                               │
│                                                           │
│  ✅ Cloud SQL                                            │
│     • db-f1-micro instance (shared-core)                │
│     • 0.6 GB RAM, 1 shared vCPU                         │
│     • 10 GB storage (first month)                        │
│                                                           │
│  ✅ Cloud Storage                                         │
│     • 5 GB standard storage                              │
│     • 5,000 Class A operations/month                     │
│     • 50,000 Class B operations/month                    │
│                                                           │
│  ✅ Secret Manager                                        │
│     • 6 secrets                                          │
│     • 10,000 secret versions                             │
│     • 10,000 access operations                           │
│                                                           │
│  ✅ Cloud Build                                           │
│     • 120 build-minutes/day                             │
│     • 10 concurrent builds                                │
│                                                           │
│  ✅ Cloud Logging                                         │
│     • 50 GB logs ingestion/month                         │
│     • 7 days log retention                                │
│                                                           │
│  ✅ Cloud Monitoring                                      │
│     • 150 MB metrics ingestion/month                     │
│     • 14 days metric retention                           │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Free Tier Limits Diagram

```
┌─────────────────────────────────────────────────────────┐
│              FREE TIER LIMITS                           │
└─────────────────────────────────────────────────────────┘

    Cloud Run
    ├── Requests: 2M/month ✅
    ├── Memory: 360K GB-seconds ✅
    └── CPU: 180K vCPU-seconds ✅
    
    Cloud SQL (db-f1-micro)
    ├── Instance: 1 shared-core ✅
    ├── RAM: 0.6 GB ✅
    └── Storage: 10 GB (first month) ✅
    
    Cloud Storage
    ├── Storage: 5 GB ✅
    ├── Class A: 5K ops/month ✅
    └── Class B: 50K ops/month ✅
    
    Secret Manager
    ├── Secrets: 6 ✅
    ├── Versions: 10K ✅
    └── Access: 10K ops/month ✅
```

---

## 🏗️ Architecture Diagram

### Free Tier Architecture

```
┌─────────────────────────────────────────────────────────┐
│         FREE TIER ARCHITECTURE                          │
└─────────────────────────────────────────────────────────┘

                    ┌──────────────┐
                    │   Internet   │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │  Cloud Run   │
                    │  (Free Tier) │
                    │              │
                    │  NiceCommerce│
                    │  Application │
                    └──────┬───────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Cloud SQL   │  │  Cloud       │  │  Secret      │
│  (db-f1-micro)│  │  Storage     │  │  Manager     │
│              │  │  (5 GB)       │  │  (6 secrets) │
│  MySQL 8.0   │  │              │  │              │
│  0.6 GB RAM  │  │  Media Files │  │  Credentials │
└──────────────┘  └──────────────┘  └──────────────┘
        │
        ▼
┌──────────────┐
│  Firebase    │
│  (External)  │
│  Auth        │
└──────────────┘
```

### Deployment Flow Diagram

```
┌─────────────────────────────────────────────────────────┐
│              DEPLOYMENT FLOW                             │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Developer   │
    │  Pushes Code │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Cloud Build  │
    │  (Free Tier)  │
    │  120 min/day  │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Build Image │
    │  (Docker)     │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Push to     │
    │  Artifact    │
    │  Registry    │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Deploy to   │
    │  Cloud Run   │
    │  (Free Tier) │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Application  │
    │  Running      │
    └──────────────┘
```

---

## 🔧 Prerequisites

### Required Accounts

```
┌─────────────────────────────────────────────────────────┐
│         PREREQUISITES                                   │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  1. Google Cloud Platform Account                        │
│     • Sign up at: https://cloud.google.com              │
│     • Free $300 credit (90 days)                         │
│     • Credit card required (won't be charged)             │
│                                                           │
│  2. Firebase Account                                     │
│     • Linked to GCP project                              │
│     • Free tier available                                │
│                                                           │
│  3. Local Tools                                          │
│     • Google Cloud SDK (gcloud)                          │
│     • Docker (for local testing)                        │
│     • Maven 3.9+                                         │
│     • Java 17                                            │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Install Google Cloud SDK

```bash
# macOS
brew install google-cloud-sdk

# Linux
curl https://sdk.cloud.google.com | bash

# Windows
# Download from: https://cloud.google.com/sdk/docs/install

# Verify installation
gcloud --version
```

---

## 📝 Step 1: GCP Project Setup

### Create GCP Project

```bash
# Login to GCP
gcloud auth login

# Create new project (or use existing)
gcloud projects create nicecommerce-free \
    --name="NiceCommerce Free Tier"

# Set as default project
gcloud config set project nicecommerce-free

# Get project ID
PROJECT_ID=$(gcloud config get-value project)
echo "Project ID: $PROJECT_ID"

# Enable billing (required even for free tier)
# Go to: https://console.cloud.google.com/billing
# Link a billing account (won't be charged if within free tier)
```

### Project Setup Diagram

```
┌─────────────────────────────────────────────────────────┐
│         PROJECT SETUP FLOW                              │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Create      │
    │  GCP Account │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Create      │
    │  Project     │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Link        │
    │  Billing     │
    │  Account     │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Enable      │
    │  APIs        │
    └──────────────┘
```

---

## 🚀 Step 2: Enable Free Tier Services

### Enable Required APIs

```bash
# Set project ID
export PROJECT_ID=$(gcloud config get-value project)

# Enable all required APIs (all have free tier)
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

### API Enablement Diagram

```
┌─────────────────────────────────────────────────────────┐
│         API ENABLEMENT                                   │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  GCP Project │
    └──────┬───────┘
           │
           ▼
    ┌─────────────────────────────────────┐
    │  Enable APIs (Free Tier)             │
    │  ├── Cloud Run API                   │
    │  ├── Cloud SQL API                   │
    │  ├── Cloud Storage API               │
    │  ├── Secret Manager API              │
    │  ├── Cloud Build API                 │
    │  ├── Logging API                     │
    │  └── Monitoring API                  │
    └─────────────────────────────────────┘
```

---

## 🗄️ Step 3: Cloud SQL Setup (Free Tier)

### Create Cloud SQL Instance (db-f1-micro)

```bash
# Create MySQL instance (FREE TIER)
gcloud sql instances create nicecommerce-db \
    --database-version=MYSQL_8_0 \
    --tier=db-f1-micro \
    --region=us-central1 \
    --root-password=YOUR_SECURE_PASSWORD \
    --storage-type=SSD \
    --storage-size=10GB \
    --storage-auto-increase \
    --backup-start-time=03:00 \
    --enable-bin-log \
    --maintenance-window-day=SUN \
    --maintenance-window-hour=4 \
    --deletion-protection=false

# Note: db-f1-micro is FREE TIER eligible
# - 0.6 GB RAM
# - 1 shared vCPU
# - 10 GB storage (first month free)
```

### Create Database and User

```bash
# Create database
gcloud sql databases create nicecommerce \
    --instance=nicecommerce-db \
    --charset=utf8mb4 \
    --collation=utf8mb4_unicode_ci

# Create database user
gcloud sql users create nicecommerce-user \
    --instance=nicecommerce-db \
    --password=YOUR_SECURE_PASSWORD

# Get connection name
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe nicecommerce-db \
    --format="value(connectionName)")
echo "Connection name: ${INSTANCE_CONNECTION_NAME}"
```

### Cloud SQL Architecture

```
┌─────────────────────────────────────────────────────────┐
│         CLOUD SQL FREE TIER                             │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Cloud SQL   │
    │  Instance    │
    │              │
    │  db-f1-micro │
    │  ├── 0.6 GB  │
    │  ├── 1 vCPU  │
    │  └── 10 GB   │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  MySQL 8.0    │
    │  Database     │
    │              │
    │  nicecommerce│
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  User:        │
    │  nicecommerce│
    │  -user        │
    └──────────────┘
```

---

## 📦 Step 4: Cloud Storage Setup

### Create Storage Bucket

```bash
# Create bucket (FREE TIER: 5 GB)
gsutil mb -p ${PROJECT_ID} -c STANDARD -l us-central1 \
    gs://${PROJECT_ID}-nicecommerce-media

# Set bucket permissions (public read for images)
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

### Cloud Storage Structure

```
┌─────────────────────────────────────────────────────────┐
│         CLOUD STORAGE STRUCTURE                          │
└─────────────────────────────────────────────────────────┘

    gs://PROJECT_ID-nicecommerce-media/
    ├── products/
    │   ├── images/
    │   │   ├── product-1.jpg
    │   │   └── product-2.jpg
    │   └── thumbnails/
    │       └── ...
    ├── users/
    │   └── avatars/
    │       └── ...
    └── temp/
        └── uploads/
```

---

## 🔐 Step 5: Secret Manager Setup

### Create Secrets (Free Tier: 6 secrets)

```bash
# 1. Database Password
echo -n "your-secure-db-password" | \
    gcloud secrets create db-password \
    --data-file=-

# 2. Firebase Service Account (from JSON file)
gcloud secrets create firebase-service-account \
    --data-file=path/to/firebase-service-account.json

# 3. JWT Secret
echo -n "your-256-bit-secret-key-minimum-32-characters-long" | \
    gcloud secrets create jwt-secret \
    --data-file=-

# 4. MercadoPago Access Token (if using)
echo -n "your-mercadopago-access-token" | \
    gcloud secrets create mercadopago-access-token \
    --data-file=-

# 5. MercadoPago Webhook Secret (if using)
echo -n "your-mercadopago-webhook-secret" | \
    gcloud secrets create mercadopago-webhook-secret \
    --data-file=-

# 6. SMTP Password (if using email)
echo -n "your-smtp-password" | \
    gcloud secrets create smtp-password \
    --data-file=-

# List secrets
gcloud secrets list
```

### Secret Manager Architecture

```
┌─────────────────────────────────────────────────────────┐
│         SECRET MANAGER (6 secrets free)                 │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Secret      │
    │  Manager     │
    └──────┬───────┘
           │
           ├── db-password
           ├── firebase-service-account
           ├── jwt-secret
           ├── mercadopago-access-token
           ├── mercadopago-webhook-secret
           └── smtp-password
```

---

## 🚢 Step 6: Cloud Run Deployment

### Build and Deploy

```bash
# Set variables
export PROJECT_ID=$(gcloud config get-value project)
export REGION=us-central1
export SERVICE_NAME=nicecommerce

# Get instance connection name
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe nicecommerce-db \
    --format="value(connectionName)")

# Build Docker image locally (or use Cloud Build)
docker build -t gcr.io/${PROJECT_ID}/${SERVICE_NAME}:latest .

# Push to Container Registry (or Artifact Registry)
docker push gcr.io/${PROJECT_ID}/${SERVICE_NAME}:latest

# Deploy to Cloud Run (FREE TIER)
gcloud run deploy ${SERVICE_NAME} \
    --image gcr.io/${PROJECT_ID}/${SERVICE_NAME}:latest \
    --platform managed \
    --region ${REGION} \
    --allow-unauthenticated \
    --add-cloudsql-instances ${INSTANCE_CONNECTION_NAME} \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars="GCP_PROJECT_ID=${PROJECT_ID}" \
    --set-env-vars="GCP_CLOUD_SQL_INSTANCE=${INSTANCE_CONNECTION_NAME}" \
    --set-env-vars="DATABASE_NAME=nicecommerce" \
    --set-env-vars="DATABASE_USERNAME=nicecommerce-user" \
    --set-env-vars="GCP_STORAGE_BUCKET=${PROJECT_ID}-nicecommerce-media" \
    --set-env-vars="FIREBASE_PROJECT_ID=${PROJECT_ID}" \
    --set-secrets="DATABASE_PASSWORD=db-password:latest" \
    --set-secrets="FIREBASE_CREDENTIALS_PATH=firebase-service-account:latest" \
    --set-secrets="JWT_SECRET=jwt-secret:latest" \
    --memory=512Mi \
    --cpu=1 \
    --timeout=300 \
    --max-instances=10 \
    --min-instances=0 \
    --concurrency=80 \
    --port=8080

# Get service URL
SERVICE_URL=$(gcloud run services describe ${SERVICE_NAME} \
    --region ${REGION} \
    --format="value(status.url)")

echo "Service deployed at: ${SERVICE_URL}"
```

### Cloud Run Configuration Diagram

```
┌─────────────────────────────────────────────────────────┐
│         CLOUD RUN FREE TIER CONFIG                       │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Cloud Run   │
    │  Service     │
    │              │
    │  Memory:     │
    │  512 Mi      │
    │              │
    │  CPU: 1      │
    │              │
    │  Min: 0      │
    │  Max: 10     │
    │              │
    │  Timeout:    │
    │  300s        │
    └──────┬───────┘
           │
           ├── Connects to Cloud SQL
           ├── Reads from Secret Manager
           ├── Writes to Cloud Storage
           └── Authenticates with Firebase
```

---

## 🔥 Step 7: Firebase Integration

### Firebase Setup

```bash
# 1. Go to Firebase Console
# https://console.firebase.google.com/

# 2. Create/Select Firebase project
# (Should be linked to your GCP project)

# 3. Enable Authentication
# - Go to Authentication > Sign-in method
# - Enable Email/Password
# - Enable Google (optional)

# 4. Download service account key
# - Go to Project Settings > Service Accounts
# - Click "Generate New Private Key"
# - Save as firebase-service-account.json

# 5. Upload to Secret Manager
gcloud secrets create firebase-service-account \
    --data-file=firebase-service-account.json

# 6. Update Cloud Run to use Firebase credentials
gcloud run services update nicecommerce \
    --set-secrets="FIREBASE_CREDENTIALS_PATH=firebase-service-account:latest" \
    --region=us-central1
```

### Firebase Integration Flow

```
┌─────────────────────────────────────────────────────────┐
│         FIREBASE INTEGRATION                             │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Client      │
    │  (Mobile/Web)│
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Firebase    │
    │  Auth        │
    │              │
    │  - Sign Up   │
    │  - Sign In   │
    │  - Token     │
    └──────┬───────┘
           │
           │ ID Token
           ▼
    ┌──────────────┐
    │  Cloud Run   │
    │  (Backend)   │
    │              │
    │  - Verify    │
    │  - Extract   │
    │  - Authorize │
    └──────────────┘
```

---

## 🔄 Step 8: CI/CD with Cloud Build

### Create Cloud Build Configuration

Create `cloudbuild.yaml`:

```yaml
steps:
  # Build the application
  - name: 'maven:3.9-eclipse-temurin-17'
    entrypoint: 'mvn'
    args: ['clean', 'package', '-DskipTests']
    dir: '.'

  # Build Docker image
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'build'
      - '-t'
      - 'gcr.io/${PROJECT_ID}/nicecommerce:${SHORT_SHA}'
      - '-t'
      - 'gcr.io/${PROJECT_ID}/nicecommerce:latest'
      - '.'

  # Push Docker image
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - 'gcr.io/${PROJECT_ID}/nicecommerce:${SHORT_SHA}'
  
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - 'gcr.io/${PROJECT_ID}/nicecommerce:latest'

  # Deploy to Cloud Run
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: gcloud
    args:
      - 'run'
      - 'deploy'
      - 'nicecommerce'
      - '--image'
      - 'gcr.io/${PROJECT_ID}/nicecommerce:${SHORT_SHA}'
      - '--region'
      - 'us-central1'
      - '--platform'
      - 'managed'
      - '--allow-unauthenticated'
      - '--add-cloudsql-instances'
      - '${PROJECT_ID}:us-central1:nicecommerce-db'
      - '--set-env-vars'
      - 'SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=${PROJECT_ID},GCP_CLOUD_SQL_INSTANCE=${PROJECT_ID}:us-central1:nicecommerce-db,DATABASE_NAME=nicecommerce,DATABASE_USERNAME=nicecommerce-user,GCP_STORAGE_BUCKET=${PROJECT_ID}-nicecommerce-media,FIREBASE_PROJECT_ID=${PROJECT_ID}'
      - '--set-secrets'
      - 'DATABASE_PASSWORD=db-password:latest,FIREBASE_CREDENTIALS_PATH=firebase-service-account:latest,JWT_SECRET=jwt-secret:latest'
      - '--memory'
      - '512Mi'
      - '--cpu'
      - '1'
      - '--timeout'
      - '300'
      - '--max-instances'
      - '10'
      - '--min-instances'
      - '0'

images:
  - 'gcr.io/${PROJECT_ID}/nicecommerce:${SHORT_SHA}'
  - 'gcr.io/${PROJECT_ID}/nicecommerce:latest'

options:
  machineType: 'E2_HIGHCPU_8'
  logging: CLOUD_LOGGING_ONLY

timeout: '1200s'
```

### CI/CD Pipeline Diagram

```
┌─────────────────────────────────────────────────────────┐
│         CI/CD PIPELINE (FREE TIER)                      │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Git Push    │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Cloud Build │
    │  120 min/day │
    │  (Free)      │
    └──────┬───────┘
           │
           ├── Build (Maven)
           ├── Test
           ├── Build Docker Image
           └── Deploy to Cloud Run
```

---

## 💰 Cost Optimization

### Free Tier Optimization Tips

```
┌─────────────────────────────────────────────────────────┐
│         COST OPTIMIZATION                               │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  1. Cloud Run                                            │
│     • Set min-instances=0 (scale to zero)               │
│     • Use 512Mi memory (minimum)                         │
│     • Set max-instances=10 (limit)                      │
│                                                           │
│  2. Cloud SQL                                            │
│     • Use db-f1-micro (free tier)                        │
│     • 10 GB storage (first month free)                   │
│     • Enable auto-increase (but monitor)                 │
│                                                           │
│  3. Cloud Storage                                        │
│     • Use Standard storage (cheapest)                    │
│     • Enable lifecycle policies                          │
│     • Compress images                                    │
│                                                           │
│  4. Cloud Build                                          │
│     • Optimize build time                                │
│     • Use build cache                                    │
│     • Limit concurrent builds                            │
│                                                           │
│  5. Monitoring                                           │
│     • Set up budget alerts                               │
│     • Monitor usage daily                                │
│     • Use free tier limits                               │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Budget Alert Setup

```bash
# Create budget alert (to avoid unexpected charges)
gcloud billing budgets create \
    --billing-account=YOUR_BILLING_ACCOUNT_ID \
    --display-name="NiceCommerce Free Tier Budget" \
    --budget-amount=1USD \
    --threshold-rule=percent=50 \
    --threshold-rule=percent=90 \
    --threshold-rule=percent=100 \
    --notification-rule=pubsub-topic=projects/${PROJECT_ID}/topics/budget-alerts
```

---

## 📊 Monitoring & Alerts

### Enable Monitoring

```bash
# View logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce" \
    --limit 50

# View metrics
gcloud monitoring time-series list \
    --filter='resource.type="cloud_run_revision"'

# Check service status
gcloud run services describe nicecommerce \
    --region=us-central1
```

### Monitoring Dashboard

```
┌─────────────────────────────────────────────────────────┐
│         MONITORING DASHBOARD                            │
└─────────────────────────────────────────────────────────┘

    Cloud Run Metrics
    ├── Request Count
    ├── Request Latency
    ├── Error Rate
    └── Instance Count
    
    Cloud SQL Metrics
    ├── CPU Usage
    ├── Memory Usage
    ├── Storage Usage
    └── Connection Count
    
    Cloud Storage Metrics
    ├── Storage Used
    ├── Operations Count
    └── Bandwidth
```

---

## 🐛 Troubleshooting

### Common Issues

```bash
# 1. Cloud SQL Connection Failed
gcloud sql instances describe nicecommerce-db
gcloud sql connect nicecommerce-db --user=nicecommerce-user

# 2. Secret Access Denied
gcloud secrets get-iam-policy db-password
gcloud secrets add-iam-policy-binding db-password \
    --member="serviceAccount:${PROJECT_NUMBER}-compute@developer.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"

# 3. Cloud Run Deployment Failed
gcloud run services describe nicecommerce \
    --region=us-central1 \
    --format="yaml(status.conditions)"

# 4. Check Logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce" \
    --limit 50 \
    --format json
```

---

## ✅ Verification Checklist

```
┌─────────────────────────────────────────────────────────┐
│         DEPLOYMENT VERIFICATION                         │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ✅ GCP Project created                                  │
│  ✅ Billing account linked                               │
│  ✅ APIs enabled                                         │
│  ✅ Cloud SQL instance created (db-f1-micro)            │
│  ✅ Database and user created                           │
│  ✅ Cloud Storage bucket created                         │
│  ✅ Secrets created in Secret Manager                    │
│  ✅ Firebase project configured                          │
│  ✅ Cloud Run service deployed                           │
│  ✅ Health endpoint accessible                           │
│  ✅ Database connection working                           │
│  ✅ Secrets accessible                                   │
│  ✅ CI/CD pipeline configured                            │
│  ✅ Monitoring enabled                                   │
│  ✅ Budget alerts configured                             │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## 📚 Quick Reference Commands

```bash
# View service URL
gcloud run services describe nicecommerce --region=us-central1 --format="value(status.url)"

# View logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce" --limit 50

# Update service
gcloud run services update nicecommerce --region=us-central1

# Connect to database
gcloud sql connect nicecommerce-db --user=nicecommerce-user

# View secrets
gcloud secrets list

# Check free tier usage
gcloud billing accounts list
```

---

**Last Updated**: 2024  
**Status**: ✅ Free Tier Ready  
**Estimated Monthly Cost**: $0 (within free tier limits)

