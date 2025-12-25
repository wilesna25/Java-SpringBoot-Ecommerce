# Jenkins Setup Guide - Expert Level Configuration
## NiceCommerce Spring Boot CI/CD Pipeline

Complete guide for setting up Jenkins as a senior expert/ninja level for the NiceCommerce Spring Boot project.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Installation Options](#installation-options)
4. [Configuration](#configuration)
5. [Pipeline Setup](#pipeline-setup)
6. [Security Hardening](#security-hardening)
7. [Monitoring & Maintenance](#monitoring--maintenance)
8. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This guide sets up a **production-ready Jenkins** instance with:

- ✅ Multi-stage CI/CD pipeline
- ✅ Automated testing and code coverage
- ✅ Security scanning
- ✅ Docker image building and deployment
- ✅ GCP integration
- ✅ Blue-green deployments
- ✅ Rollback capability
- ✅ Comprehensive monitoring

---

## 🔧 Prerequisites

### Required Software

1. **Docker & Docker Compose**
   ```bash
   docker --version
   docker-compose --version
   ```

2. **Google Cloud SDK**
   ```bash
   gcloud --version
   gcloud auth login
   ```

3. **Kubectl** (if using Kubernetes)
   ```bash
   kubectl version --client
   ```

4. **Maven** (optional, included in Jenkins image)
   ```bash
   mvn --version
   ```

### Required GCP Resources

- GCP Project with billing enabled
- Artifact Registry repository
- Cloud SQL instance
- Service account with required permissions
- Secret Manager secrets configured

### Required Credentials

- GCP Service Account Key (JSON)
- GitHub Personal Access Token (for webhooks)
- Slack Webhook URL (for notifications)
- SonarQube Token (optional)

---

## 🚀 Installation Options

### Option 1: Docker Compose (Recommended for Development)

**Quick Start:**

```bash
cd nicecommerce-springboot/jenkins

# Set environment variables
export JENKINS_ADMIN_PASSWORD="your-secure-password"
export GCP_PROJECT_ID="your-project-id"
export GCP_REGION="us-central1"

# Start Jenkins
docker-compose up -d

# View logs
docker-compose logs -f jenkins

# Access Jenkins
# URL: http://localhost:8080
# Username: admin
# Password: (from JENKINS_ADMIN_PASSWORD)
```

### Option 2: Kubernetes Deployment

**Deploy to GKE:**

```bash
# Create namespace
kubectl create namespace jenkins

# Create secrets
kubectl create secret generic jenkins-admin \
    --from-literal=password='your-secure-password' \
    -n jenkins

kubectl create secret generic gcp-service-account \
    --from-file=key.json=path/to/service-account.json \
    -n jenkins

# Deploy Jenkins
kubectl apply -f jenkins/k8s/jenkins-deployment.yaml
kubectl apply -f jenkins/k8s/jenkins-service.yaml

# Get service URL
kubectl get svc jenkins -n jenkins
```

### Option 3: Google Cloud Run (Serverless)

**Deploy Jenkins to Cloud Run:**

```bash
# Build Jenkins image
docker build -t gcr.io/${PROJECT_ID}/jenkins:latest jenkins/

# Push to Artifact Registry
docker push gcr.io/${PROJECT_ID}/jenkins:latest

# Deploy to Cloud Run
gcloud run deploy jenkins \
    --image gcr.io/${PROJECT_ID}/jenkins:latest \
    --platform managed \
    --region us-central1 \
    --port 8080 \
    --memory 2Gi \
    --cpu 2 \
    --timeout 3600 \
    --set-env-vars="JENKINS_OPTS=--httpPort=8080" \
    --allow-unauthenticated
```

---

## ⚙️ Configuration

### Step 1: Initial Jenkins Setup

1. **Access Jenkins**
   - URL: `http://localhost:8080` (or your deployed URL)
   - Username: `admin`
   - Password: (from environment variable or initial setup)

2. **Install Recommended Plugins**
   - Jenkins will prompt on first login
   - Or install manually from `Manage Jenkins → Plugins`

3. **Verify Plugin Installation**
   ```bash
   # Check installed plugins
   curl -u admin:password http://localhost:8080/pluginManager/api/json?depth=1
   ```

### Step 2: Configure Credentials

**Add GCP Service Account:**

1. Go to `Manage Jenkins → Credentials → System → Global credentials`
2. Click `Add Credentials`
3. Select `Secret file`
4. Upload GCP service account JSON
5. ID: `gcp-service-account-key`
6. Description: `GCP Service Account for deployments`

**Add GitHub Credentials:**

1. Create GitHub Personal Access Token
2. Add credentials:
   - Kind: `Secret text`
   - Secret: `<your-github-token>`
   - ID: `github-credentials`
   - Description: `GitHub Personal Access Token`

**Add GCP Project ID:**

1. Add credentials:
   - Kind: `Secret text`
   - Secret: `<your-gcp-project-id>`
   - ID: `gcp-project-id`
   - Description: `GCP Project ID`

**Add Slack Token (Optional):**

1. Create Slack App and get webhook URL
2. Add credentials:
   - Kind: `Secret text`
   - Secret: `<slack-webhook-url>`
   - ID: `slack-token`
   - Description: `Slack Webhook URL`

### Step 3: Configure Tools

**Configure JDK:**

1. Go to `Manage Jenkins → Global Tool Configuration`
2. JDK installations:
   - Name: `JDK-17`
   - JAVA_HOME: `/usr/lib/jvm/java-17-openjdk`

**Configure Maven:**

1. Maven installations:
   - Name: `Maven-3.9`
   - MAVEN_HOME: `/usr/share/maven`

**Configure Docker:**

1. Ensure Docker socket is mounted: `/var/run/docker.sock`
2. Verify: `docker ps` works in Jenkins

### Step 4: Configure Email (SMTP)

1. Go to `Manage Jenkins → Configure System`
2. E-mail Notification:
   - SMTP server: `smtp.gmail.com`
   - Default user e-mail suffix: `@nicecommerce.com`
   - Use SMTP Authentication: Yes
   - User Name: `<your-email>`
   - Password: `<app-password>`
   - Test configuration

---

## 🔄 Pipeline Setup

### Step 1: Create Pipeline Job

1. **New Item**
   - Click `New Item`
   - Name: `nicecommerce-ci-cd`
   - Type: `Pipeline`
   - Click `OK`

2. **Configure Pipeline**
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `https://github.com/yourusername/nicecommerce-springboot.git`
   - Credentials: `github-credentials`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`

3. **Build Triggers**
   - ✅ GitHub hook trigger for GITScm polling
   - ✅ Poll SCM: `H/5 * * * *` (every 5 minutes)
   - ✅ Build periodically: (optional)

4. **Save**

### Step 2: Configure GitHub Webhook

1. **In GitHub Repository:**
   - Go to `Settings → Webhooks → Add webhook`
   - Payload URL: `http://your-jenkins-url/github-webhook/`
   - Content type: `application/json`
   - Events: `Just the push event`
   - Active: ✅
   - Click `Add webhook`

2. **Test Webhook:**
   ```bash
   # Make a commit and push
   git commit --allow-empty -m "Test webhook"
   git push origin main
   ```

### Step 3: Run First Build

1. Click `Build Now` on the pipeline job
2. Monitor build progress in `Blue Ocean` or classic view
3. Check console output for any errors

---

## 🔒 Security Hardening

### 1. Enable HTTPS

```bash
# Generate SSL certificate
openssl req -x509 -newkey rsa:4096 -keyout jenkins-key.pem \
    -out jenkins-cert.pem -days 365 -nodes

# Update docker-compose.yml
environment:
  - JENKINS_OPTS=--httpPort=-1 --httpsPort=8443 \
    --httpsKeyStore=/var/jenkins_home/keystore.jks \
    --httpsKeyStorePassword=changeit
```

### 2. Configure Security Realm

- Use `Matrix-based security` or `Role-based strategy`
- Create user accounts (don't use admin for daily work)
- Assign appropriate permissions

### 3. Enable CSRF Protection

- Go to `Manage Jenkins → Configure Global Security`
- ✅ Enable CSRF protection
- ✅ Enable CLI over Remoting

### 4. Restrict Agent Access

```groovy
// In Jenkinsfile
agent {
    label 'docker && maven'
}
```

### 5. Secret Management

- Never commit secrets to repository
- Use Jenkins Credentials Store
- Use GCP Secret Manager for production secrets
- Rotate credentials regularly

### 6. Network Security

- Use firewall rules to restrict access
- Enable VPN for remote access
- Use reverse proxy (Nginx/Traefik)

---

## 📊 Monitoring & Maintenance

### 1. Jenkins Health Monitoring

```bash
# Health check endpoint
curl http://localhost:8080/api/json?tree=nodeName,nodeDescription,executors[progress]

# System information
curl http://localhost:8080/systemInfo
```

### 2. Build Metrics

- Install `Metrics Plugin`
- View build trends
- Monitor build duration
- Track failure rates

### 3. Disk Space Management

```bash
# Configure build retention
# In Jenkinsfile:
options {
    buildDiscarder(logRotator(
        numToKeepStr: '10',
        daysToKeepStr: '30',
        artifactDaysToKeepStr: '7'
    ))
}
```

### 4. Backup Strategy

```bash
# Backup Jenkins home directory
tar -czf jenkins-backup-$(date +%Y%m%d).tar.gz /var/jenkins_home

# Restore
tar -xzf jenkins-backup-YYYYMMDD.tar.gz -C /var/jenkins_home
```

### 5. Plugin Updates

- Regularly update plugins
- Test updates in staging first
- Keep backup before updates

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Build Fails: "Maven not found"

**Solution:**
```bash
# Verify Maven installation
docker exec jenkins mvn --version

# Rebuild Jenkins image with Maven
docker-compose build --no-cache jenkins
```

#### 2. Docker Permission Denied

**Solution:**
```bash
# Add jenkins user to docker group
docker exec jenkins usermod -aG docker jenkins

# Or fix permissions
chmod 666 /var/run/docker.sock
```

#### 3. GCP Authentication Failed

**Solution:**
```bash
# Verify service account key
docker exec jenkins gcloud auth activate-service-account \
    --key-file=/path/to/key.json

# Check credentials in Jenkins
# Manage Jenkins → Credentials
```

#### 4. Pipeline Hangs

**Solution:**
- Check agent availability
- Verify resource limits
- Check network connectivity
- Review build logs

#### 5. Code Coverage Below Threshold

**Solution:**
- Review test coverage report
- Add missing tests
- Adjust threshold if needed (temporarily)

---

## 📈 Advanced Features

### 1. Parallel Test Execution

Already configured in Jenkinsfile:
```groovy
parallel {
    stage('Unit Tests') { ... }
    stage('Integration Tests') { ... }
}
```

### 2. Blue-Green Deployment

Configured in production deployment stage:
- Deploy to new revision with `--no-traffic`
- Health check
- Route traffic to new revision

### 3. Rollback Capability

```bash
# Rollback to previous revision
gcloud run services update-traffic nicecommerce \
    --region us-central1 \
    --to-revisions REVISION_NAME=100
```

### 4. Multi-Environment Deployment

- Staging: `develop` branch
- Production: `main` branch
- Feature: `feature/*` branches (optional)

---

## ✅ Verification Checklist

After setup, verify:

- [ ] Jenkins is accessible
- [ ] All plugins installed
- [ ] Credentials configured
- [ ] Tools configured (JDK, Maven, Docker)
- [ ] Pipeline job created
- [ ] GitHub webhook configured
- [ ] First build successful
- [ ] Tests running
- [ ] Code coverage report generated
- [ ] Docker image built
- [ ] Deployment to staging works
- [ ] Notifications working
- [ ] Security configured
- [ ] Backups configured

---

## 🎓 Best Practices

1. **Use Pipeline as Code** - Store Jenkinsfile in repository
2. **Version Control** - Track all configuration changes
3. **Infrastructure as Code** - Use Docker Compose or Kubernetes
4. **Secrets Management** - Never hardcode secrets
5. **Test Everything** - Run tests before deployment
6. **Monitor Builds** - Set up alerts for failures
7. **Regular Updates** - Keep Jenkins and plugins updated
8. **Backup Regularly** - Automate backup process
9. **Document Changes** - Keep runbook updated
10. **Security First** - Follow security best practices

---

## 📚 Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Jenkins Best Practices](https://www.jenkins.io/doc/book/pipeline/pipeline-best-practices/)
- [GCP Jenkins Integration](https://cloud.google.com/solutions/continuous-integration-jenkins-kubernetes-engine)

---

**Status**: ✅ Production Ready  
**Last Updated**: After Jenkins expert-level configuration

