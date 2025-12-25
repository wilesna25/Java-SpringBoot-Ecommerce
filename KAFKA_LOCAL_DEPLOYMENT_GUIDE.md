# 🚀 Kafka Local Deployment Guide
## Testing Kafka Implementation with Docker Desktop for Windows

> **Author**: Senior Java/Spring Boot Expert (15+ years experience)  
> **Target**: Local development and testing with Docker Desktop on Windows  
> **Kafka Version**: 7.5.0 (Confluent Platform)

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Docker Desktop Setup](#docker-desktop-setup)
3. [Starting Kafka Services](#starting-kafka-services)
4. [Verifying Kafka Setup](#verifying-kafka-setup)
5. [Running the Application](#running-the-application)
6. [Testing Kafka Events](#testing-kafka-events)
7. [Monitoring with Kafka UI](#monitoring-with-kafka-ui)
8. [Troubleshooting](#troubleshooting)
9. [Common Commands](#common-commands)

---

## 🔧 Prerequisites

### Required Software

```
┌─────────────────────────────────────────────────────────┐
│         PREREQUISITES                                   │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ✅ Docker Desktop for Windows                           │
│     • Version 4.0+                                       │
│     • Download: https://www.docker.com/products/docker-desktop │
│                                                           │
│  ✅ Java 17                                              │
│     • JDK 17 or higher                                  │
│                                                           │
│  ✅ Maven 3.9+                                           │
│     • For building the application                      │
│                                                           │
│  ✅ Git                                                  │
│     • For cloning the repository                        │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### System Requirements

- **Windows 10/11** (64-bit)
- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: At least 10GB free
- **WSL 2** (Windows Subsystem for Linux 2) - Required for Docker Desktop

---

## 🐳 Docker Desktop Setup

### Step 1: Install Docker Desktop

1. **Download Docker Desktop**
   - Go to: https://www.docker.com/products/docker-desktop
   - Download Docker Desktop for Windows
   - Run the installer

2. **Enable WSL 2** (if not already enabled)
   ```powershell
   # Open PowerShell as Administrator
   wsl --install
   ```

3. **Start Docker Desktop**
   - Launch Docker Desktop from Start Menu
   - Wait for Docker to start (whale icon in system tray)
   - Verify Docker is running:
     ```powershell
     docker --version
     docker-compose --version
     ```

### Step 2: Configure Docker Desktop

1. **Open Docker Desktop Settings**
   - Click the gear icon (⚙️)
   - Go to **Resources** → **Advanced**

2. **Recommended Settings**
   ```
   CPUs: 4+ (or half of your total CPUs)
   Memory: 4GB+ (or 25% of total RAM)
   Swap: 1GB
   Disk image size: 60GB+
   ```

3. **Enable WSL 2 Integration**
   - Go to **Settings** → **Resources** → **WSL Integration**
   - Enable integration with your WSL 2 distro

---

## 🚀 Starting Kafka Services

### Step 1: Navigate to Project Directory

```powershell
# Open PowerShell or Command Prompt
cd C:\Github\Java-SpringBoot-Ecommerce
```

### Step 2: Start Kafka Services with Docker Compose

```powershell
# Start all services (Kafka, Zookeeper, MySQL, Kafka UI)
docker-compose up -d

# Or start in foreground to see logs
docker-compose up
```

### Step 3: Verify Services are Running

```powershell
# Check running containers
docker-compose ps

# Expected output:
# nicecommerce-zookeeper    Up
# nicecommerce-kafka        Up
# nicecommerce-kafka-ui     Up
# nicecommerce-mysql        Up
```

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│         LOCAL KAFKA ARCHITECTURE                        │
└─────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │  Application │
    │  (Port 8080) │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │    Kafka     │
    │  (Port 9092) │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Zookeeper   │
    │  (Port 2181) │
    └──────────────┘
    
    ┌──────────────┐
    │  Kafka UI    │
    │  (Port 8081) │
    └──────────────┘
    
    ┌──────────────┐
    │    MySQL     │
    │  (Port 3306) │
    └──────────────┘
```

---

## ✅ Verifying Kafka Setup

### Step 1: Check Kafka is Running

```powershell
# Check Kafka container logs
docker-compose logs kafka

# Look for: "started (kafka.server.KafkaServer)"
```

### Step 2: List Kafka Topics

```powershell
# Execute command inside Kafka container
docker exec -it nicecommerce-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Expected topics:
# __consumer_offsets
# idempotency-keys
# order-events
# payment-events
# product-events
```

### Step 3: Check Topic Details

```powershell
# Check order-events topic
docker exec -it nicecommerce-kafka kafka-topics --bootstrap-server localhost:9092 \
  --describe --topic order-events

# Expected output:
# Topic: order-events    PartitionCount: 3    ReplicationFactor: 1
```

### Step 4: Test Producer/Consumer

```powershell
# Start a console producer
docker exec -it nicecommerce-kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic order-events

# In another terminal, start a console consumer
docker exec -it nicecommerce-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning
```

---

## 🏃 Running the Application

### Step 1: Build the Application

```powershell
# Clean and build
mvn clean install -DskipTests

# Or with tests
mvn clean install
```

### Step 2: Configure Application

Ensure `application.yml` has Kafka configuration:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: nicecommerce-group
```

### Step 3: Run the Application

```powershell
# Run Spring Boot application
mvn spring-boot:run

# Or run the JAR
java -jar target/nicecommerce-springboot-1.0.0.jar
```

### Step 4: Verify Application Started

- Check logs for: `Started NiceCommerceApplication`
- Check logs for: `Kafka topics created`
- Open browser: http://localhost:8080/actuator/health

---

## 🧪 Testing Kafka Events

### Test 1: Create Order Event

```powershell
# Using curl (PowerShell)
$body = @{
    userId = 1
    idempotencyKey = "test-key-123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/orders" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

### Test 2: Monitor Events in Real-Time

```powershell
# Consumer for order-events
docker exec -it nicecommerce-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning \
  --property print.key=true \
  --property print.value=true
```

### Test 3: Check Idempotency Events

```powershell
# Consumer for idempotency-keys
docker exec -it nicecommerce-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic idempotency-keys \
  --from-beginning \
  --property print.key=true \
  --property print.value=true
```

### Test 4: Send Test Message

```powershell
# Producer for testing
docker exec -it nicecommerce-kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic order-events

# Then type:
{"eventType":"ORDER_CREATED","orderId":1,"userId":1,"timestamp":"2024-01-01T10:00:00"}
```

---

## 📊 Monitoring with Kafka UI

### Access Kafka UI

1. **Open Browser**
   - URL: http://localhost:8081

2. **View Topics**
   - Click on **Topics** in the left menu
   - You should see:
     - `order-events`
     - `payment-events`
     - `product-events`
     - `idempotency-keys`

3. **View Messages**
   - Click on a topic
   - Click **Messages** tab
   - View real-time messages

4. **View Consumers**
   - Click on **Consumers** in the left menu
   - See consumer groups and their lag

### Kafka UI Features

```
┌─────────────────────────────────────────────────────────┐
│         KAFKA UI FEATURES                               │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ✅ View all topics and partitions                       │
│  ✅ Browse messages in topics                            │
│  ✅ View consumer groups                                 │
│  ✅ Monitor consumer lag                                 │
│  ✅ Create/delete topics                                 │
│  ✅ View topic configuration                             │
│  ✅ Search messages                                      │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## 🐛 Troubleshooting

### Issue 1: Docker Desktop Not Starting

**Symptoms**: Docker Desktop fails to start

**Solutions**:
```powershell
# 1. Check WSL 2 is installed
wsl --list --verbose

# 2. Update WSL 2
wsl --update

# 3. Restart Docker Desktop
# Right-click Docker Desktop icon → Restart
```

### Issue 2: Kafka Container Fails to Start

**Symptoms**: `nicecommerce-kafka` container exits immediately

**Solutions**:
```powershell
# 1. Check logs
docker-compose logs kafka

# 2. Check Zookeeper is running
docker-compose ps zookeeper

# 3. Restart services
docker-compose down
docker-compose up -d
```

### Issue 3: Application Can't Connect to Kafka

**Symptoms**: `Connection refused` or `Bootstrap server not available`

**Solutions**:
```powershell
# 1. Verify Kafka is running
docker-compose ps kafka

# 2. Check Kafka is accessible
docker exec -it nicecommerce-kafka kafka-broker-api-versions \
  --bootstrap-server localhost:9092

# 3. Check application.yml has correct bootstrap-servers
# Should be: localhost:9092
```

### Issue 4: Port Already in Use

**Symptoms**: `Port 9092 is already in use`

**Solutions**:
```powershell
# 1. Find process using port
netstat -ano | findstr :9092

# 2. Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F

# 3. Or change port in docker-compose.yml
# Change "9092:9092" to "9094:9092"
```

### Issue 5: Topics Not Created Automatically

**Symptoms**: Topics don't appear in Kafka UI

**Solutions**:
```powershell
# 1. Manually create topics
docker exec -it nicecommerce-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --topic order-events \
  --partitions 3 \
  --replication-factor 1

# 2. Or restart application (topics are auto-created)
```

---

## 📝 Common Commands

### Docker Compose Commands

```powershell
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f kafka

# Restart a specific service
docker-compose restart kafka

# Remove all containers and volumes
docker-compose down -v
```

### Kafka Commands

```powershell
# List topics
docker exec -it nicecommerce-kafka kafka-topics \
  --bootstrap-server localhost:9092 --list

# Describe topic
docker exec -it nicecommerce-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe --topic order-events

# Create topic manually
docker exec -it nicecommerce-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --create --topic test-topic \
  --partitions 3 --replication-factor 1

# Delete topic
docker exec -it nicecommerce-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete --topic test-topic

# Consumer groups
docker exec -it nicecommerce-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 --list
```

### Application Commands

```powershell
# Build application
mvn clean install

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test
```

---

## ✅ Verification Checklist

After setup, verify:

- [ ] Docker Desktop is running
- [ ] All containers are up (`docker-compose ps`)
- [ ] Kafka is accessible on port 9092
- [ ] Zookeeper is running on port 2181
- [ ] Kafka UI is accessible on http://localhost:8081
- [ ] MySQL is running on port 3306
- [ ] Application starts without errors
- [ ] Topics are created automatically
- [ ] Can produce/consume messages
- [ ] Events appear in Kafka UI

---

## 🎯 Quick Start Summary

```powershell
# 1. Start services
docker-compose up -d

# 2. Wait 30 seconds for services to start
Start-Sleep -Seconds 30

# 3. Verify Kafka is running
docker exec -it nicecommerce-kafka kafka-topics --bootstrap-server localhost:9092 --list

# 4. Build and run application
mvn clean install
mvn spring-boot:run

# 5. Open Kafka UI
Start-Process "http://localhost:8081"

# 6. Test by creating an order via API
```

---

## 📚 Additional Resources

- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Docker Desktop Documentation](https://docs.docker.com/desktop/)
- [Spring Kafka Documentation](https://docs.spring.io/spring-kafka/docs/current/reference/html/)

---

## 🔄 Next Steps

1. **Test Event Production**: Create orders and verify events are published
2. **Test Event Consumption**: Verify consumers process events correctly
3. **Monitor Performance**: Use Kafka UI to monitor throughput
4. **Scale Testing**: Test with multiple consumers and producers
5. **Error Handling**: Test failure scenarios and retry logic

---

**Last Updated**: 2024  
**Status**: ✅ Ready for Local Testing  
**Maintained By**: Senior Java/Spring Boot Expert Team

