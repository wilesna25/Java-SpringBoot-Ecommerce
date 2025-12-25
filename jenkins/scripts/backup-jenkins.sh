#!/bin/bash

# Jenkins Backup Script
# Backs up Jenkins home directory and configuration

set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-/backups/jenkins}"
JENKINS_HOME="${JENKINS_HOME:-/var/jenkins_home}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"
DATE=$(date +%Y%m%d_%H%M%S)

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Starting Jenkins backup...${NC}"

# Create backup directory
mkdir -p "${BACKUP_DIR}"

# Create backup
BACKUP_FILE="${BACKUP_DIR}/jenkins-backup-${DATE}.tar.gz"
echo "Creating backup: ${BACKUP_FILE}"

tar -czf "${BACKUP_FILE}" \
    --exclude='workspace' \
    --exclude='*.log' \
    --exclude='tmp' \
    "${JENKINS_HOME}"

# Verify backup
if [ -f "${BACKUP_FILE}" ]; then
    BACKUP_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
    echo -e "${GREEN}✅ Backup created successfully: ${BACKUP_SIZE}${NC}"
else
    echo -e "${YELLOW}❌ Backup failed!${NC}"
    exit 1
fi

# Clean old backups
echo "Cleaning backups older than ${RETENTION_DAYS} days..."
find "${BACKUP_DIR}" -name "jenkins-backup-*.tar.gz" -mtime +${RETENTION_DAYS} -delete

# List backups
echo -e "${GREEN}Current backups:${NC}"
ls -lh "${BACKUP_DIR}"/jenkins-backup-*.tar.gz 2>/dev/null || echo "No backups found"

echo -e "${GREEN}Backup completed!${NC}"

