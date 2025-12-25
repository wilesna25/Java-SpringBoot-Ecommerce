# Jenkins Quick Start Guide

## 🚀 5-Minute Setup

### Prerequisites Check

```bash
docker --version
docker-compose --version
gcloud --version
```

### Start Jenkins

```bash
cd nicecommerce-springboot/jenkins

# Set environment variables
export JENKINS_ADMIN_PASSWORD="admin123"
export GCP_PROJECT_ID="your-project-id"

# Start Jenkins
docker-compose up -d

# View logs
docker-compose logs -f jenkins
```

### Access Jenkins

- **URL**: http://localhost:8080
- **Username**: `admin`
- **Password**: `admin123` (or your JENKINS_ADMIN_PASSWORD)

### Configure Pipeline

1. **Install Plugins** (if prompted)
2. **Add Credentials**:
   - GCP Service Account: `Manage Jenkins → Credentials`
   - GitHub Token: `Manage Jenkins → Credentials`
3. **Create Pipeline Job**:
   - `New Item → Pipeline`
   - Name: `nicecommerce-ci-cd`
   - Pipeline script from SCM
   - Repository: Your GitHub repo
   - Script Path: `Jenkinsfile`

### Run First Build

Click `Build Now` and watch the magic happen! ✨

---

**For detailed setup, see**: [JENKINS_SETUP_GUIDE.md](../JENKINS_SETUP_GUIDE.md)

