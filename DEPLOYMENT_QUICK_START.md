# Quick Start Deployment Guide

## 🚀 Fast Track Deployment

This is a condensed version of the full deployment guide. For detailed instructions, see [DEPLOYMENT_GUIDE_FIREBASE_GCP.md](DEPLOYMENT_GUIDE_FIREBASE_GCP.md).

---

## Prerequisites Checklist

- [ ] GCP account with billing enabled
- [ ] Firebase project created
- [ ] Google Cloud SDK installed (`gcloud`)
- [ ] Docker installed
- [ ] Authenticated: `gcloud auth login`

---

## Step 1: Initial Setup (5 minutes)

```bash
# Set variables
export PROJECT_ID="your-project-id"
export REGION="us-central1"

# Set project
gcloud config set project ${PROJECT_ID}

# Enable APIs
gcloud services enable \
    cloudbuild.googleapis.com \
    run.googleapis.com \
    sqladmin.googleapis.com \
    storage-component.googleapis.com \
    secretmanager.googleapis.com
```

---

## Step 2: Create Secrets (10 minutes)

```bash
# Run setup script
./scripts/setup-secrets.sh

# Or manually create secrets:
# 1. Firebase service account
gcloud secrets create firebase-service-account \
    --data-file=firebase-service-account.json

# 2. Database password
echo -n "your-password" | gcloud secrets create db-password --data-file=-

# 3. JWT secret
echo -n "your-256-bit-secret" | gcloud secrets create jwt-secret --data-file=-
```

---

## Step 3: Create Database (5 minutes)

```bash
# Create Cloud SQL instance
gcloud sql instances create nicecommerce-db \
    --database-version=MYSQL_8.0 \
    --tier=db-f1-micro \
    --region=${REGION}

# Create database
gcloud sql databases create nicecommerce --instance=nicecommerce-db

# Create user
gcloud sql users create nicecommerce-user \
    --instance=nicecommerce-db \
    --password=$(gcloud secrets versions access latest --secret="db-password")
```

---

## Step 4: Create Storage (2 minutes)

```bash
# Create bucket
gsutil mb -p ${PROJECT_ID} -l ${REGION} \
    gs://${PROJECT_ID}-nicecommerce-media
```

---

## Step 5: Build and Deploy (10 minutes)

```bash
# Option 1: Use deployment script
./scripts/deploy.sh

# Option 2: Manual deployment
# Build image
docker build -t gcr.io/${PROJECT_ID}/nicecommerce:latest .

# Push image
docker push gcr.io/${PROJECT_ID}/nicecommerce:latest

# Deploy to Cloud Run
gcloud run deploy nicecommerce \
    --image gcr.io/${PROJECT_ID}/nicecommerce:latest \
    --region ${REGION} \
    --platform managed \
    --allow-unauthenticated \
    --add-cloudsql-instances=${PROJECT_ID}:${REGION}:nicecommerce-db \
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=${PROJECT_ID}" \
    --set-secrets="DATABASE_PASSWORD=projects/${PROJECT_ID}/secrets/db-password/versions/latest"
```

---

## Step 6: Verify (2 minutes)

```bash
# Get service URL
SERVICE_URL=$(gcloud run services describe nicecommerce \
    --region ${REGION} \
    --format="value(status.url)")

# Test health endpoint
curl ${SERVICE_URL}/actuator/health

# Should return: {"status":"UP"}
```

---

## Common Commands

```bash
# View logs
gcloud logging read "resource.type=cloud_run_revision" --limit 50

# Update service
gcloud run services update nicecommerce --region=${REGION}

# View service details
gcloud run services describe nicecommerce --region=${REGION}

# Access secret
gcloud secrets versions access latest --secret="secret-name"

# Connect to database
gcloud sql connect nicecommerce-db --user=nicecommerce-user
```

---

## Troubleshooting

### Service won't start
```bash
# Check logs
gcloud logging read "resource.type=cloud_run_revision" --limit 20

# Check service status
gcloud run services describe nicecommerce --region=${REGION}
```

### Database connection failed
```bash
# Verify instance exists
gcloud sql instances list

# Test connection
gcloud sql connect nicecommerce-db --user=nicecommerce-user
```

### Secret access denied
```bash
# Grant access
gcloud secrets add-iam-policy-binding secret-name \
    --member="serviceAccount:${PROJECT_ID}@appspot.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
```

---

## Next Steps

1. Set up CI/CD pipeline (see full guide)
2. Configure custom domain
3. Set up monitoring and alerts
4. Enable Cloud Armor for DDoS protection
5. Configure backup strategy

---

**For detailed instructions, see**: [DEPLOYMENT_GUIDE_FIREBASE_GCP.md](DEPLOYMENT_GUIDE_FIREBASE_GCP.md)

