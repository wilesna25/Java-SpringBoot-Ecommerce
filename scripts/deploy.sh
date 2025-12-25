#!/bin/bash

# NiceCommerce Deployment Script
# This script automates the deployment process to Google Cloud Platform

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ID=${GCP_PROJECT_ID:-$(gcloud config get-value project)}
REGION=${GCP_REGION:-us-central1}
SERVICE_NAME=${SERVICE_NAME:-nicecommerce}
DB_INSTANCE=${DB_INSTANCE:-nicecommerce-db}
REPO_NAME=${REPO_NAME:-nicecommerce-repo}
IMAGE_NAME=${IMAGE_NAME:-nicecommerce}

echo -e "${GREEN}Starting deployment to GCP...${NC}"
echo "Project ID: ${PROJECT_ID}"
echo "Region: ${REGION}"
echo "Service: ${SERVICE_NAME}"

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo -e "${RED}Error: gcloud CLI is not installed${NC}"
    exit 1
fi

# Check if user is authenticated
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo -e "${YELLOW}Not authenticated. Please run: gcloud auth login${NC}"
    exit 1
fi

# Set project
echo -e "${GREEN}Setting project to ${PROJECT_ID}...${NC}"
gcloud config set project ${PROJECT_ID}

# Get instance connection name
INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe ${DB_INSTANCE} \
    --format="value(connectionName)" 2>/dev/null || echo "")

if [ -z "$INSTANCE_CONNECTION_NAME" ]; then
    echo -e "${RED}Error: Cloud SQL instance '${DB_INSTANCE}' not found${NC}"
    exit 1
fi

echo "Instance connection: ${INSTANCE_CONNECTION_NAME}"

# Build Docker image
echo -e "${GREEN}Building Docker image...${NC}"
IMAGE_TAG="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest"
docker build -t ${IMAGE_TAG} .

# Push image
echo -e "${GREEN}Pushing image to Artifact Registry...${NC}"
docker push ${IMAGE_TAG}

# Deploy to Cloud Run
echo -e "${GREEN}Deploying to Cloud Run...${NC}"
gcloud run deploy ${SERVICE_NAME} \
    --image ${IMAGE_TAG} \
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
    --set-secrets="DATABASE_PASSWORD=projects/${PROJECT_ID}/secrets/db-password/versions/latest" \
    --set-secrets="FIREBASE_CREDENTIALS_PATH=projects/${PROJECT_ID}/secrets/firebase-service-account/versions/latest" \
    --set-secrets="JWT_SECRET=projects/${PROJECT_ID}/secrets/jwt-secret/versions/latest" \
    --set-secrets="MERCADOPAGO_ACCESS_TOKEN=projects/${PROJECT_ID}/secrets/mercadopago-access-token/versions/latest" \
    --set-secrets="MERCADOPAGO_WEBHOOK_SECRET=projects/${PROJECT_ID}/secrets/mercadopago-webhook-secret/versions/latest" \
    --set-secrets="MAIL_PASSWORD=projects/${PROJECT_ID}/secrets/smtp-password/versions/latest" \
    --set-secrets="CORS_ALLOWED_ORIGINS=projects/${PROJECT_ID}/secrets/cors-allowed-origins/versions/latest" \
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

echo -e "${GREEN}Deployment complete!${NC}"
echo -e "${GREEN}Service URL: ${SERVICE_URL}${NC}"
echo -e "${GREEN}Health check: ${SERVICE_URL}/actuator/health${NC}"

# Test health endpoint
echo -e "${GREEN}Testing health endpoint...${NC}"
sleep 5
curl -f ${SERVICE_URL}/actuator/health || echo -e "${YELLOW}Health check failed (service may still be starting)${NC}"

echo -e "${GREEN}Done!${NC}"

