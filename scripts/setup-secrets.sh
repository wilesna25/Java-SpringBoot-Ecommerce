#!/bin/bash

# Script to set up all required secrets in Google Cloud Secret Manager

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

PROJECT_ID=${GCP_PROJECT_ID:-$(gcloud config get-value project)}

echo -e "${GREEN}Setting up secrets for project: ${PROJECT_ID}${NC}"

# Check if project is set
if [ -z "$PROJECT_ID" ]; then
    echo -e "${RED}Error: PROJECT_ID not set. Set GCP_PROJECT_ID or run: gcloud config set project YOUR_PROJECT_ID${NC}"
    exit 1
fi

# Function to create or update secret
create_secret() {
    local secret_name=$1
    local description=$2
    local input_method=$3  # "file", "stdin", or "prompt"
    local file_path=$4
    
    if gcloud secrets describe ${secret_name} &>/dev/null; then
        echo -e "${YELLOW}Secret ${secret_name} already exists. Updating...${NC}"
        if [ "$input_method" = "file" ]; then
            gcloud secrets versions add ${secret_name} --data-file=${file_path}
        elif [ "$input_method" = "stdin" ]; then
            read -sp "Enter value for ${secret_name}: " value
            echo -n "$value" | gcloud secrets versions add ${secret_name} --data-file=-
        else
            read -sp "Enter value for ${secret_name}: " value
            echo -n "$value" | gcloud secrets versions add ${secret_name} --data-file=-
        fi
    else
        echo -e "${GREEN}Creating secret: ${secret_name}${NC}"
        if [ "$input_method" = "file" ]; then
            gcloud secrets create ${secret_name} \
                --data-file=${file_path} \
                --replication-policy="automatic"
        elif [ "$input_method" = "stdin" ]; then
            read -sp "Enter value for ${secret_name}: " value
            echo -n "$value" | gcloud secrets create ${secret_name} \
                --data-file=- \
                --replication-policy="automatic"
        else
            read -sp "Enter value for ${secret_name}: " value
            echo -n "$value" | gcloud secrets create ${secret_name} \
                --data-file=- \
                --replication-policy="automatic"
        fi
    fi
}

# 1. Firebase Service Account Key
echo -e "\n${GREEN}1. Firebase Service Account Key${NC}"
read -p "Path to firebase-service-account.json (or press Enter to skip): " firebase_path
if [ -n "$firebase_path" ] && [ -f "$firebase_path" ]; then
    create_secret "firebase-service-account" "Firebase Admin SDK service account key" "file" "$firebase_path"
else
    echo -e "${YELLOW}Skipping Firebase service account key${NC}"
fi

# 2. Database Password
echo -e "\n${GREEN}2. Database Password${NC}"
create_secret "db-password" "MySQL database password" "prompt" ""

# 3. JWT Secret
echo -e "\n${GREEN}3. JWT Secret${NC}"
create_secret "jwt-secret" "JWT signing secret (minimum 32 characters)" "prompt" ""

# 4. MercadoPago Access Token
echo -e "\n${GREEN}4. MercadoPago Access Token${NC}"
read -p "Do you want to set MercadoPago token? (y/n): " set_mp
if [ "$set_mp" = "y" ]; then
    create_secret "mercadopago-access-token" "MercadoPago access token" "prompt" ""
fi

# 5. MercadoPago Webhook Secret
echo -e "\n${GREEN}5. MercadoPago Webhook Secret${NC}"
read -p "Do you want to set MercadoPago webhook secret? (y/n): " set_mp_wh
if [ "$set_mp_wh" = "y" ]; then
    create_secret "mercadopago-webhook-secret" "MercadoPago webhook secret" "prompt" ""
fi

# 6. SMTP Password
echo -e "\n${GREEN}6. SMTP Password${NC}"
read -p "Do you want to set SMTP password? (y/n): " set_smtp
if [ "$set_smtp" = "y" ]; then
    create_secret "smtp-password" "SMTP email password" "prompt" ""
fi

# 7. CORS Allowed Origins
echo -e "\n${GREEN}7. CORS Allowed Origins${NC}"
read -p "Enter CORS allowed origins (comma-separated, e.g., https://example.com,https://www.example.com): " cors_origins
if [ -n "$cors_origins" ]; then
    echo -n "$cors_origins" | gcloud secrets create cors-allowed-origins \
        --data-file=- \
        --replication-policy="automatic" 2>/dev/null || \
    echo -n "$cors_origins" | gcloud secrets versions add cors-allowed-origins --data-file=-
fi

# Grant access to Cloud Run service account
echo -e "\n${GREEN}Granting secret access to Cloud Run service account...${NC}"
SERVICE_ACCOUNT="${PROJECT_ID}@appspot.gserviceaccount.com"

SECRETS=(
    "firebase-service-account"
    "db-password"
    "jwt-secret"
    "mercadopago-access-token"
    "mercadopago-webhook-secret"
    "smtp-password"
    "cors-allowed-origins"
)

for secret in "${SECRETS[@]}"; do
    if gcloud secrets describe ${secret} &>/dev/null; then
        echo "Granting access to ${secret}..."
        gcloud secrets add-iam-policy-binding ${secret} \
            --member="serviceAccount:${SERVICE_ACCOUNT}" \
            --role="roles/secretmanager.secretAccessor" \
            --quiet || true
    fi
done

echo -e "\n${GREEN}Secrets setup complete!${NC}"
echo -e "${GREEN}List of secrets:${NC}"
gcloud secrets list

