# Deployment Guide - NiceCommerce Spring Boot

Complete guide for deploying NiceCommerce Spring Boot to Google Cloud Platform.

## Prerequisites

1. Google Cloud Platform account
2. Google Cloud SDK installed (`gcloud`)
3. Docker installed (for local testing)
4. Maven 3.6+

## Step 1: GCP Project Setup

```bash
# Set your project
gcloud config set project YOUR_PROJECT_ID

# Enable required APIs
gcloud services enable \
    cloudbuild.googleapis.com \
    run.googleapis.com \
    sqladmin.googleapis.com \
    storage-component.googleapis.com \
    secretmanager.googleapis.com
```

## Step 2: Cloud SQL Database Setup

```bash
# Create Cloud SQL instance
gcloud sql instances create nicecommerce-db \
    --database-version=MYSQL_8_0 \
    --tier=db-f1-micro \
    --region=us-central1 \
    --root-password=YOUR_ROOT_PASSWORD

# Create database
gcloud sql databases create nicecommerce --instance=nicecommerce-db

# Create database user
gcloud sql users create nicecommerce \
    --instance=nicecommerce-db \
    --password=YOUR_DB_PASSWORD
```

## Step 3: Cloud Storage Setup

```bash
# Create storage bucket for media files
gsutil mb -p YOUR_PROJECT_ID -l us-central1 gs://nicecommerce-media

# Set bucket permissions (make readable)
gsutil iam ch allUsers:objectViewer gs://nicecommerce-media
```

## Step 4: Secret Manager Setup

```bash
# Store database password
echo -n "YOUR_DB_PASSWORD" | gcloud secrets create db-password --data-file=-

# Store JWT secret
echo -n "your-256-bit-secret-key" | gcloud secrets create jwt-secret --data-file=-

# Store MercadoPago token
echo -n "YOUR_MERCADOPAGO_TOKEN" | gcloud secrets create mercadopago-token --data-file=-
```

## Step 5: Build and Deploy

### Option A: Cloud Build (Recommended)

```bash
# Submit build
gcloud builds submit --config cloudbuild.yaml
```

### Option B: Manual Build

```bash
# Build Docker image
docker build -t gcr.io/YOUR_PROJECT_ID/nicecommerce .

# Push to Container Registry
docker push gcr.io/YOUR_PROJECT_ID/nicecommerce

# Deploy to Cloud Run
gcloud run deploy nicecommerce \
    --image gcr.io/YOUR_PROJECT_ID/nicecommerce \
    --platform managed \
    --region us-central1 \
    --allow-unauthenticated \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars="GCP_PROJECT_ID=YOUR_PROJECT_ID" \
    --set-env-vars="GCP_CLOUD_SQL_INSTANCE=YOUR_PROJECT_ID:us-central1:nicecommerce-db" \
    --set-env-vars="DATABASE_NAME=nicecommerce" \
    --set-env-vars="GCP_STORAGE_BUCKET=nicecommerce-media" \
    --add-cloudsql-instances=YOUR_PROJECT_ID:us-central1:nicecommerce-db \
    --memory=512Mi \
    --cpu=1 \
    --timeout=300 \
    --max-instances=10
```

## Step 6: Environment Variables

Set these in Cloud Run or use Secret Manager:

```yaml
SPRING_PROFILES_ACTIVE: prod
DATABASE_URL: jdbc:mysql:///nicecommerce?cloudSqlInstance=PROJECT:REGION:INSTANCE&socketFactory=com.google.cloud.sql.mysql.SocketFactory
DATABASE_USERNAME: nicecommerce
DATABASE_PASSWORD: (from Secret Manager)
JWT_SECRET: (from Secret Manager)
GCP_PROJECT_ID: YOUR_PROJECT_ID
GCP_STORAGE_BUCKET: nicecommerce-media
MERCADOPAGO_ACCESS_TOKEN: (from Secret Manager)
CORS_ALLOWED_ORIGINS: https://yourdomain.com
```

## Step 7: Database Migrations

Run migrations using Cloud SQL Proxy or Cloud Shell:

```bash
# Connect via Cloud SQL Proxy
cloud_sql_proxy -instances=YOUR_PROJECT_ID:us-central1:nicecommerce-db=tcp:3306

# Run migrations (if using Flyway/Liquibase)
# Or let JPA create schema (development only)
```

## Step 8: Verify Deployment

```bash
# Check service status
gcloud run services describe nicecommerce --region us-central1

# Test health endpoint
curl https://YOUR_SERVICE_URL/actuator/health

# Test API
curl https://YOUR_SERVICE_URL/api/products
```

## Monitoring and Logging

### View Logs

```bash
# Cloud Run logs
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=nicecommerce" --limit 50
```

### Metrics

- Cloud Run metrics available in GCP Console
- Spring Boot Actuator metrics at `/actuator/metrics`

## Scaling

Cloud Run automatically scales based on traffic:

- **Min instances**: 0 (pay only for usage)
- **Max instances**: Set based on expected load
- **Concurrency**: 80 requests per instance (default)

## Cost Optimization

1. Use Cloud SQL with appropriate tier
2. Enable Cloud CDN for static assets
3. Use Cloud Storage lifecycle policies
4. Monitor and adjust Cloud Run limits

## Troubleshooting

### Connection Issues

- Verify Cloud SQL instance is running
- Check Cloud SQL Proxy connection
- Verify database credentials

### Performance Issues

- Check Cloud Run metrics
- Review database query performance
- Enable Redis caching
- Optimize JPA queries

## Security Best Practices

1. Use Secret Manager for sensitive data
2. Enable Cloud Armor for DDoS protection
3. Use IAM roles with least privilege
4. Enable Cloud SQL SSL connections
5. Regular security updates

---

**For local development**, use `application-dev.yml` with local MySQL and Redis.

