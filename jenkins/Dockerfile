# Jenkins Master Dockerfile
# Production-ready Jenkins with all required plugins

FROM jenkins/jenkins:lts-jdk17

# Switch to root to install packages
USER root

# Install required tools
RUN apt-get update && apt-get install -y \
    docker.io \
    docker-compose \
    maven \
    curl \
    wget \
    git \
    unzip \
    python3 \
    python3-pip \
    gcloud \
    kubectl \
    && rm -rf /var/lib/apt/lists/*

# Install Google Cloud SDK
RUN curl https://sdk.cloud.google.com | bash && \
    export PATH=$PATH:/root/google-cloud-sdk/bin && \
    gcloud components install kubectl docker-credential-gcr --quiet

# Install Docker Compose
RUN curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" \
    -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# Install Trivy (container scanner)
RUN wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | apt-key add - && \
    echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | tee -a /etc/apt/sources.list.d/trivy.list && \
    apt-get update && \
    apt-get install -y trivy

# Install Jenkins plugins
COPY jenkins/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Copy Jenkins configuration
COPY jenkins/jenkins.yaml /usr/share/jenkins/ref/jenkins.yaml
COPY jenkins/init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Set Jenkins environment variables
ENV JAVA_OPTS="-Xmx2048m -Xms512m -Djenkins.install.runSetupWizard=false"
ENV JENKINS_OPTS="--httpPort=8080 --prefix=/jenkins"

# Create Jenkins directories
RUN mkdir -p /var/jenkins_home/workspace && \
    chown -R jenkins:jenkins /var/jenkins_home

# Switch back to jenkins user
USER jenkins

# Expose Jenkins port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/login || exit 1

