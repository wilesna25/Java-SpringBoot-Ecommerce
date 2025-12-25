#!/bin/bash

# Jenkins Restore Script
# Restores Jenkins from backup

set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-/backups/jenkins}"
JENKINS_HOME="${JENKINS_HOME:-/var/jenkins_home}"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}⚠️  WARNING: This will overwrite current Jenkins data!${NC}"
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Restore cancelled."
    exit 0
fi

# List available backups
echo -e "${GREEN}Available backups:${NC}"
ls -lh "${BACKUP_DIR}"/jenkins-backup-*.tar.gz 2>/dev/null || {
    echo -e "${RED}No backups found in ${BACKUP_DIR}${NC}"
    exit 1
}

# Select backup
read -p "Enter backup filename (or 'latest' for most recent): " backup_file

if [ "$backup_file" = "latest" ]; then
    backup_file=$(ls -t "${BACKUP_DIR}"/jenkins-backup-*.tar.gz | head -1)
    backup_file=$(basename "$backup_file")
fi

BACKUP_PATH="${BACKUP_DIR}/${backup_file}"

if [ ! -f "${BACKUP_PATH}" ]; then
    echo -e "${RED}Backup file not found: ${BACKUP_PATH}${NC}"
    exit 1
fi

echo -e "${GREEN}Restoring from: ${backup_file}${NC}"

# Stop Jenkins (if running as service)
# systemctl stop jenkins || docker stop jenkins || true

# Backup current state (safety)
CURRENT_BACKUP="${BACKUP_DIR}/pre-restore-$(date +%Y%m%d_%H%M%S).tar.gz"
echo "Creating safety backup of current state..."
tar -czf "${CURRENT_BACKUP}" "${JENKINS_HOME}" || true

# Restore
echo "Extracting backup..."
tar -xzf "${BACKUP_PATH}" -C "$(dirname ${JENKINS_HOME})"

# Fix permissions
echo "Fixing permissions..."
chown -R jenkins:jenkins "${JENKINS_HOME}" || true

# Start Jenkins (if stopped)
# systemctl start jenkins || docker start jenkins || true

echo -e "${GREEN}✅ Restore completed!${NC}"
echo -e "${YELLOW}Please restart Jenkins to apply changes.${NC}"

