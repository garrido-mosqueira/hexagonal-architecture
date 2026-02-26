# Task API WebFlux - Setup Guide

## Prerequisites

- Docker and Docker Compose installed
- Java 17+ (check pom.xml for required version)
- Maven 3.8+

## Running the Application

### Step 1: Start RabbitMQ and MongoDB

From the project root directory (`api-webflux`), run:

```bash
docker-compose up -d
```

This will start:
- **RabbitMQ**: Available at `http://localhost:15672` (Management UI: guest/guest)
- **MongoDB**: Available at `localhost:27017`

Verify services are running:
```bash
docker-compose ps
```

### Step 2: Run the Spring Boot Application

```bash
mvn clean spring-boot:run
```

Or build and run the JAR:
```bash
mvn clean package
java -jar target/task-api-webflux-1.1-SNAPSHOT.jar
```

The application will be available at `http://localhost:8080`

## Testing the Application

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### List Tasks
```bash
curl http://localhost:8080/api/tasks
```

### Reactive Endpoint (Consumes messages from RabbitMQ)
```bash
curl http://localhost:8080/api/reactive/
```

This endpoint streams messages from RabbitMQ as Server-Sent Events (SSE).

## Stopping Services

To stop the Docker containers:
```bash
docker-compose down
```

To stop and remove volumes:
```bash
docker-compose down -v
```

## Configuration

Application properties are located in `src/main/resources/application.properties`:

- **RabbitMQ**: `localhost:5672`
- **MongoDB**: `localhost:27017`
- **Server Port**: `8080`
- **Management Endpoints**: All endpoints exposed at `/actuator`

## Troubleshooting

### RabbitMQ Connection Error
If you get `UnknownHostException: rabbitmq`, ensure:
1. Docker containers are running: `docker-compose ps`
2. RabbitMQ is healthy: `docker-compose logs rabbitmq`
3. Port 5672 is available

### MongoDB Connection Error
If MongoDB connection fails:
1. Check container is running: `docker-compose ps`
2. Verify port 27017 is available

### Rebuild Docker Images
To rebuild from latest base images:
```bash
docker-compose down
docker-compose pull
docker-compose up -d
```
