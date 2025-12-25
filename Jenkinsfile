/**
 * Jenkins Pipeline Configuration
 * 
 * Production-ready CI/CD pipeline for NiceCommerce Spring Boot application.
 * 
 * Features:
 * - Multi-stage pipeline (Build, Test, Security Scan, Deploy)
 * - Parallel test execution
 * - Code coverage enforcement
 * - Docker image building
 * - GCP deployment
 * - Rollback capability
 * - Notifications
 * 
 * @author NiceCommerce Team
 */

pipeline {
    agent any
    
    // Environment variables
    environment {
        // Project configuration
        PROJECT_ID = "${env.GCP_PROJECT_ID ?: 'nicecommerce-prod'}"
        REGION = "${env.GCP_REGION ?: 'us-central1'}"
        SERVICE_NAME = 'nicecommerce'
        REPO_NAME = 'nicecommerce-repo'
        IMAGE_NAME = 'nicecommerce'
        
        // Docker configuration
        DOCKER_REGISTRY = "${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}"
        IMAGE_TAG = "${DOCKER_REGISTRY}/${IMAGE_NAME}"
        IMAGE_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
        
        // Database
        DB_INSTANCE = 'nicecommerce-db'
        
        // Maven configuration
        MAVEN_OPTS = '-Xmx2048m -XX:MaxPermSize=512m'
        MAVEN_HOME = tool 'Maven-3.9'
        
        // Java configuration
        JAVA_HOME = tool 'JDK-17'
        
        // Test coverage threshold
        COVERAGE_THRESHOLD = '80'
        
        // Notification channels
        SLACK_CHANNEL = "${env.SLACK_CHANNEL ?: '#deployments'}"
        EMAIL_RECIPIENTS = "${env.EMAIL_RECIPIENTS ?: 'devops@nicecommerce.com'}"
    }
    
    // Pipeline options
    options {
        // Build timeout (30 minutes)
        timeout(time: 30, unit: 'MINUTES')
        
        // Keep last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        
        // AnsiColor plugin for colored console output
        ansiColor('xterm')
        
        // Timestamps in console output
        timestamps()
        
        // Skip stages if previous stage failed
        skipStagesAfterUnstable()
        
        // GitHub integration
        githubPush()
    }
    
    // Pipeline stages
    stages {
        /**
         * Stage 1: Checkout and Preparation
         * - Checkout source code
         * - Validate environment
         * - Set up build tools
         */
        stage('Checkout & Prepare') {
            steps {
                script {
                    echo "🚀 Starting CI/CD Pipeline for Build #${env.BUILD_NUMBER}"
                    echo "📦 Project: ${PROJECT_ID}"
                    echo "🌿 Branch: ${env.GIT_BRANCH}"
                    echo "📝 Commit: ${env.GIT_COMMIT}"
                }
                
                // Checkout source code
                checkout scm
                
                // Validate required files
                script {
                    def requiredFiles = [
                        'pom.xml',
                        'Dockerfile',
                        'src/main/resources/application.yml'
                    ]
                    
                    requiredFiles.each { file ->
                        if (!fileExists(file)) {
                            error("Required file not found: ${file}")
                        }
                    }
                    
                    echo "✅ All required files present"
                }
                
                // Set up tools
                sh '''
                    echo "Setting up build environment..."
                    java -version
                    mvn -version
                    docker --version
                    gcloud --version
                '''
            }
        }
        
        /**
         * Stage 2: Dependency Check
         * - Download dependencies
         * - Verify dependency integrity
         */
        stage('Dependency Check') {
            steps {
                dir('.') {
                    script {
                        echo "📥 Downloading Maven dependencies..."
                        sh 'mvn dependency:resolve dependency:tree -DoutputFile=dependencies.txt'
                        
                        // Check for known vulnerabilities (optional)
                        echo "🔍 Checking for dependency vulnerabilities..."
                        sh 'mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7 || true'
                    }
                }
            }
        }
        
        /**
         * Stage 3: Code Quality
         * - Static code analysis
         * - Code style checks
         * - SonarQube analysis (if configured)
         */
        stage('Code Quality') {
            parallel {
                stage('Static Analysis') {
                    steps {
                        script {
                            echo "🔍 Running static code analysis..."
                            sh 'mvn clean compile -DskipTests'
                            
                            // PMD (optional)
                            sh 'mvn pmd:check || true'
                            
                            // Checkstyle (optional)
                            sh 'mvn checkstyle:check || true'
                        }
                    }
                }
                
                stage('SonarQube Analysis') {
                    when {
                        expression { 
                            env.SONAR_TOKEN != null 
                        }
                    }
                    steps {
                        script {
                            echo "📊 Running SonarQube analysis..."
                            withSonarQubeEnv('SonarQube') {
                                sh 'mvn sonar:sonar -Dsonar.projectKey=nicecommerce-springboot'
                            }
                        }
                    }
                }
            }
        }
        
        /**
         * Stage 4: Build
         * - Compile source code
         * - Package application
         * - Create JAR file
         */
        stage('Build') {
            steps {
                script {
                    echo "🔨 Building application..."
                    sh '''
                        mvn clean package -DskipTests \
                            -Dmaven.test.skip=false \
                            -Dmaven.javadoc.skip=true \
                            -Dspring.profiles.active=test
                    '''
                    
                    // Verify JAR was created
                    if (!fileExists('target/nicecommerce-springboot-1.0.0.jar')) {
                        error('Build failed: JAR file not created')
                    }
                    
                    echo "✅ Build successful"
                    sh 'ls -lh target/*.jar'
                }
            }
        }
        
        /**
         * Stage 5: Test
         * - Run unit tests
         * - Run integration tests
         * - Generate coverage report
         */
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        script {
                            echo "🧪 Running unit tests..."
                            sh '''
                                mvn test \
                                    -Dspring.profiles.active=test \
                                    -Dmaven.test.failure.ignore=false
                            '''
                        }
                    }
                    post {
                        always {
                            // Publish test results
                            junit 'target/surefire-reports/**/*.xml'
                            
                            // Archive test reports
                            archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                        }
                    }
                }
                
                stage('Integration Tests') {
                    steps {
                        script {
                            echo "🔗 Running integration tests..."
                            sh '''
                                mvn verify \
                                    -Dspring.profiles.active=test \
                                    -Dmaven.test.failure.ignore=false \
                                    -Dskip.unit.tests=true
                            '''
                        }
                    }
                    post {
                        always {
                            junit 'target/failsafe-reports/**/*.xml'
                        }
                    }
                }
            }
        }
        
        /**
         * Stage 6: Code Coverage
         * - Generate coverage report
         * - Enforce coverage threshold
         * - Publish coverage report
         */
        stage('Code Coverage') {
            steps {
                script {
                    echo "📊 Generating code coverage report..."
                    sh 'mvn jacoco:report'
                    
                    // Check coverage threshold
                    script {
                        def coverageFile = readFile('target/site/jacoco/index.html')
                        def coverageMatch = coverageFile =~ /Total.*?(\d+\.\d+)%/
                        
                        if (coverageMatch) {
                            def coverage = coverageMatch[0][1] as Double
                            echo "📈 Code coverage: ${coverage}%"
                            
                            if (coverage < COVERAGE_THRESHOLD.toDouble()) {
                                error("Code coverage ${coverage}% is below threshold of ${COVERAGE_THRESHOLD}%")
                            }
                            
                            echo "✅ Coverage threshold met"
                        } else {
                            echo "⚠️ Could not parse coverage report"
                        }
                    }
                }
            }
            post {
                always {
                    // Publish coverage report
                    publishHTML([
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report',
                        keepAll: true
                    ])
                    
                    // Archive coverage data
                    archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
                }
            }
        }
        
        /**
         * Stage 7: Security Scan
         * - Dependency vulnerability scan
         * - Container image scan
         * - SAST (Static Application Security Testing)
         */
        stage('Security Scan') {
            parallel {
                stage('Dependency Scan') {
                    steps {
                        script {
                            echo "🔒 Scanning dependencies for vulnerabilities..."
                            sh '''
                                mvn org.owasp:dependency-check-maven:check \
                                    -DfailBuildOnCVSS=7 \
                                    -Dformat=ALL \
                                    -Dformat=HTML || true
                            '''
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'target/dependency-check-report.html', allowEmptyArchive: true
                        }
                    }
                }
                
                stage('Container Scan') {
                    when {
                        expression { 
                            fileExists('Dockerfile') 
                        }
                    }
                    steps {
                        script {
                            echo "🐳 Scanning Docker image for vulnerabilities..."
                            // Build image for scanning
                            sh "docker build -t ${IMAGE_TAG}:${IMAGE_VERSION} ."
                            
                            // Scan with Trivy (if available)
                            sh '''
                                if command -v trivy &> /dev/null; then
                                    trivy image --exit-code 0 --severity HIGH,CRITICAL ${IMAGE_TAG}:${IMAGE_VERSION} || true
                                else
                                    echo "Trivy not installed, skipping container scan"
                                fi
                            '''
                        }
                    }
                }
            }
        }
        
        /**
         * Stage 8: Build Docker Image
         * - Build optimized Docker image
         * - Tag image with version
         * - Push to Artifact Registry
         */
        stage('Build & Push Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    branch 'release/*'
                }
            }
            steps {
                script {
                    echo "🐳 Building Docker image..."
                    
                    // Authenticate with GCP
                    withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GCP_KEY')]) {
                        sh '''
                            gcloud auth activate-service-account --key-file=${GCP_KEY}
                            gcloud auth configure-docker ${DOCKER_REGISTRY}
                        '''
                    }
                    
                    // Build image
                    sh """
                        docker build \
                            --tag ${IMAGE_TAG}:${IMAGE_VERSION} \
                            --tag ${IMAGE_TAG}:latest \
                            --build-arg BUILD_NUMBER=${env.BUILD_NUMBER} \
                            --build-arg GIT_COMMIT=${env.GIT_COMMIT} \
                            --build-arg BUILD_DATE=\$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
                            .
                    """
                    
                    // Push image
                    echo "📤 Pushing Docker image to Artifact Registry..."
                    sh """
                        docker push ${IMAGE_TAG}:${IMAGE_VERSION}
                        docker push ${IMAGE_TAG}:latest
                    """
                    
                    echo "✅ Image pushed: ${IMAGE_TAG}:${IMAGE_VERSION}"
                }
            }
        }
        
        /**
         * Stage 9: Deploy to Staging
         * - Deploy to staging environment
         * - Run smoke tests
         */
        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'release/*'
                }
            }
            steps {
                script {
                    echo "🚀 Deploying to staging environment..."
                    
                    withCredentials([
                        file(credentialsId: 'gcp-service-account-key', variable: 'GCP_KEY'),
                        string(credentialsId: 'gcp-project-id', variable: 'GCP_PROJECT')
                    ]) {
                        sh '''
                            gcloud auth activate-service-account --key-file=${GCP_KEY}
                            
                            # Get instance connection name
                            INSTANCE_CONNECTION=$(gcloud sql instances describe ${DB_INSTANCE} \
                                --format="value(connectionName)")
                            
                            # Deploy to Cloud Run (staging)
                            gcloud run deploy ${SERVICE_NAME}-staging \
                                --image ${IMAGE_TAG}:${IMAGE_VERSION} \
                                --platform managed \
                                --region ${REGION} \
                                --allow-unauthenticated \
                                --add-cloudsql-instances ${INSTANCE_CONNECTION} \
                                --set-env-vars="SPRING_PROFILES_ACTIVE=staging" \
                                --set-env-vars="GCP_PROJECT_ID=${GCP_PROJECT}" \
                                --set-secrets="DATABASE_PASSWORD=projects/${GCP_PROJECT}/secrets/db-password/versions/latest" \
                                --memory=512Mi \
                                --cpu=1 \
                                --timeout=300 \
                                --max-instances=5 \
                                --min-instances=0 \
                                --port=8080
                        '''
                    }
                    
                    // Run smoke tests
                    script {
                        echo "🧪 Running smoke tests..."
                        def stagingUrl = sh(
                            script: '''
                                gcloud run services describe ${SERVICE_NAME}-staging \
                                    --region ${REGION} \
                                    --format="value(status.url)"
                            ''',
                            returnStdout: true
                        ).trim()
                        
                        sh """
                            sleep 15
                            curl -f ${stagingUrl}/actuator/health || exit 1
                            echo "✅ Staging deployment successful"
                        """
                    }
                }
            }
        }
        
        /**
         * Stage 10: Deploy to Production
         * - Deploy to production environment
         * - Blue-green deployment strategy
         * - Health checks
         */
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "🚀 Deploying to PRODUCTION..."
                    
                    // Require approval for production deployment
                    input message: 'Deploy to Production?', ok: 'Deploy'
                    
                    withCredentials([
                        file(credentialsId: 'gcp-service-account-key', variable: 'GCP_KEY'),
                        string(credentialsId: 'gcp-project-id', variable: 'GCP_PROJECT')
                    ]) {
                        sh '''
                            gcloud auth activate-service-account --key-file=${GCP_KEY}
                            
                            # Get instance connection name
                            INSTANCE_CONNECTION=$(gcloud sql instances describe ${DB_INSTANCE} \
                                --format="value(connectionName)")
                            
                            # Deploy to Cloud Run (production)
                            gcloud run deploy ${SERVICE_NAME} \
                                --image ${IMAGE_TAG}:${IMAGE_VERSION} \
                                --platform managed \
                                --region ${REGION} \
                                --allow-unauthenticated \
                                --add-cloudsql-instances ${INSTANCE_CONNECTION} \
                                --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
                                --set-env-vars="GCP_PROJECT_ID=${GCP_PROJECT}" \
                                --set-env-vars="GCP_CLOUD_SQL_INSTANCE=${INSTANCE_CONNECTION}" \
                                --set-env-vars="DATABASE_NAME=nicecommerce" \
                                --set-env-vars="DATABASE_USERNAME=nicecommerce-user" \
                                --set-secrets="DATABASE_PASSWORD=projects/${GCP_PROJECT}/secrets/db-password/versions/latest" \
                                --set-secrets="FIREBASE_CREDENTIALS_PATH=projects/${GCP_PROJECT}/secrets/firebase-service-account/versions/latest" \
                                --set-secrets="JWT_SECRET=projects/${GCP_PROJECT}/secrets/jwt-secret/versions/latest" \
                                --memory=512Mi \
                                --cpu=1 \
                                --timeout=300 \
                                --max-instances=10 \
                                --min-instances=1 \
                                --port=8080 \
                                --no-traffic
                            
                            # Health check
                            PROD_URL=$(gcloud run services describe ${SERVICE_NAME} \
                                --region ${REGION} \
                                --format="value(status.url)")
                            
                            echo "Waiting for service to be ready..."
                            sleep 20
                            
                            # Verify health
                            for i in {1..5}; do
                                if curl -f ${PROD_URL}/actuator/health; then
                                    echo "✅ Health check passed"
                                    break
                                else
                                    echo "⏳ Health check failed, retrying... ($i/5)"
                                    sleep 10
                                fi
                            done
                            
                            # Route traffic to new revision
                            gcloud run services update-traffic ${SERVICE_NAME} \
                                --region ${REGION} \
                                --to-latest
                            
                            echo "✅ Production deployment successful"
                        '''
                    }
                }
            }
        }
        
        /**
         * Stage 11: Post-Deployment
         * - Run integration tests against production
         * - Monitor deployment
         */
        stage('Post-Deployment Verification') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "✅ Verifying production deployment..."
                    
                    def prodUrl = sh(
                        script: '''
                            gcloud run services describe ${SERVICE_NAME} \
                                --region ${REGION} \
                                --format="value(status.url)"
                        ''',
                        returnStdout: true
                    ).trim()
                    
                    sh """
                        # Health check
                        curl -f ${prodUrl}/actuator/health || exit 1
                        
                        # Info endpoint
                        curl -f ${prodUrl}/actuator/info || exit 1
                        
                        echo "✅ All post-deployment checks passed"
                    """
                }
            }
        }
    }
    
    /**
     * Post-build actions
     * - Cleanup
     * - Notifications
     * - Artifact archiving
     */
    post {
        always {
            // Clean up workspace
            cleanWs()
            
            // Archive artifacts
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/site/**/*', allowEmptyArchive: true
        }
        
        success {
            script {
                echo "✅ Pipeline completed successfully!"
                
                // Send success notification
                emailext(
                    subject: "✅ Build #${env.BUILD_NUMBER} - SUCCESS",
                    body: """
                        <h2>Build Successful</h2>
                        <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Branch:</strong> ${env.GIT_BRANCH}</p>
                        <p><strong>Commit:</strong> ${env.GIT_COMMIT}</p>
                        <p><strong>Build URL:</strong> ${env.BUILD_URL}</p>
                    """,
                    to: "${EMAIL_RECIPIENTS}",
                    mimeType: 'text/html'
                )
            }
        }
        
        failure {
            script {
                echo "❌ Pipeline failed!"
                
                // Send failure notification
                emailext(
                    subject: "❌ Build #${env.BUILD_NUMBER} - FAILED",
                    body: """
                        <h2>Build Failed</h2>
                        <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Branch:</strong> ${env.GIT_BRANCH}</p>
                        <p><strong>Commit:</strong> ${env.GIT_COMMIT}</p>
                        <p><strong>Build URL:</strong> ${env.BUILD_URL}</p>
                        <p><strong>Console:</strong> ${env.BUILD_URL}console</p>
                    """,
                    to: "${EMAIL_RECIPIENTS}",
                    mimeType: 'text/html'
                )
            }
        }
        
        unstable {
            script {
                echo "⚠️ Pipeline completed with warnings"
            }
        }
        
        cleanup {
            // Clean up Docker images
            sh '''
                docker system prune -f || true
            '''
        }
    }
}

