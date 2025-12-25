# Deployment Checklist

Use this checklist to ensure all steps are completed before going to production.

## Pre-Deployment

### Firebase Setup
- [ ] Firebase project created
- [ ] Authentication enabled (Email/Password)
- [ ] Google provider enabled (optional)
- [ ] Service account key downloaded
- [ ] Service account key stored in Secret Manager
- [ ] Authorized domains configured in Firebase

### Google Cloud Platform Setup
- [ ] GCP project created
- [ ] Billing account linked
- [ ] Required APIs enabled:
  - [ ] Cloud Build API
  - [ ] Cloud Run API
  - [ ] Cloud SQL Admin API
  - [ ] Cloud Storage API
  - [ ] Secret Manager API
  - [ ] Logging API
  - [ ] Monitoring API

### Secrets Management
- [ ] `firebase-service-account` secret created
- [ ] `db-password` secret created
- [ ] `jwt-secret` secret created
- [ ] `mercadopago-access-token` secret created (if using)
- [ ] `mercadopago-webhook-secret` secret created (if using)
- [ ] `smtp-password` secret created (if using)
- [ ] `cors-allowed-origins` secret created
- [ ] All secrets have proper IAM permissions
- [ ] Service account has Secret Accessor role

### Database Setup
- [ ] Cloud SQL instance created
- [ ] Database created (`nicecommerce`)
- [ ] Database user created
- [ ] Database migrations run (or JPA will create schema)
- [ ] Connection tested
- [ ] Backup strategy configured
- [ ] Maintenance window configured

### Storage Setup
- [ ] Cloud Storage bucket created
- [ ] Bucket permissions configured
- [ ] CORS configured (if needed)
- [ ] Lifecycle policies set (if needed)

### Application Configuration
- [ ] `application-prod.yml` configured
- [ ] Environment variables documented
- [ ] Dockerfile created and tested
- [ ] Health endpoints working
- [ ] Logging configured

## Deployment

### Build
- [ ] Docker image builds successfully
- [ ] Image pushed to Artifact Registry
- [ ] Image tagged appropriately
- [ ] Image scanned for vulnerabilities

### Deploy
- [ ] Cloud Run service created
- [ ] Environment variables set
- [ ] Secrets mounted correctly
- [ ] Cloud SQL connection configured
- [ ] Service account assigned
- [ ] Resource limits set (memory, CPU)
- [ ] Timeout configured
- [ ] Scaling configured (min/max instances)

### Verification
- [ ] Service is running
- [ ] Health endpoint responds: `/actuator/health`
- [ ] Database connection works
- [ ] Firebase authentication works
- [ ] Secrets are accessible
- [ ] Logs are being collected
- [ ] Service URL is accessible

## Post-Deployment

### Monitoring
- [ ] Cloud Monitoring enabled
- [ ] Uptime checks configured
- [ ] Alert policies created:
  - [ ] High error rate
  - [ ] Service downtime
  - [ ] High latency
  - [ ] High memory usage
- [ ] Dashboard created

### Security
- [ ] IAM roles reviewed
- [ ] Service account has minimal permissions
- [ ] Secrets rotation policy in place
- [ ] Cloud Armor configured (optional)
- [ ] VPC connector configured (if needed)
- [ ] SSL/TLS certificates configured (if custom domain)

### CI/CD
- [ ] Cloud Build trigger configured
- [ ] GitHub Actions workflow configured (if using)
- [ ] Automated tests run on deploy
- [ ] Deployment notifications set up

### Documentation
- [ ] Deployment guide documented
- [ ] Runbook created
- [ ] Troubleshooting guide created
- [ ] Team trained on deployment process

## Production Readiness

### Performance
- [ ] Load testing completed
- [ ] Performance benchmarks established
- [ ] Auto-scaling tested
- [ ] Database performance optimized

### Backup & Recovery
- [ ] Database backups automated
- [ ] Backup retention policy set
- [ ] Recovery procedure tested
- [ ] Disaster recovery plan documented

### Compliance
- [ ] Security policies reviewed
- [ ] Data retention policies set
- [ ] Privacy compliance verified
- [ ] Audit logging enabled

## Maintenance

### Regular Tasks
- [ ] Monitor costs
- [ ] Review logs weekly
- [ ] Update dependencies monthly
- [ ] Rotate secrets quarterly
- [ ] Review security policies annually

### Updates
- [ ] Application updates tested in staging
- [ ] Database migrations tested
- [ ] Rollback procedure documented
- [ ] Change log maintained

---

## Quick Verification Commands

```bash
# Check service status
gcloud run services list

# Check health
curl https://your-service-url/actuator/health

# View logs
gcloud logging read "resource.type=cloud_run_revision" --limit 20

# Check secrets
gcloud secrets list

# Check database
gcloud sql instances list

# Check storage
gsutil ls
```

---

**Last Updated**: After deployment guide creation  
**Status**: Ready for production deployment

