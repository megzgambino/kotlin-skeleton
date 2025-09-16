# Ktor REST API with PostgreSQL and Redis

A simple REST API built with Ktor, using PostgreSQL for persistence and Redis for caching.

## Prerequisites

- JDK 17 or higher
- Docker and Docker Compose
- Gradle

## Getting Started

### 1. Start the databases

```bash
docker-compose up -d
```

This will start PostgreSQL on port 5432 and Redis on port 6379.

### 2. Build the application

```bash
./gradlew build
```

### 3. Run the application

```bash
./gradlew run
```

The API will be available at http://localhost:8080

## API Endpoints

### Health Check
- `GET /health` - Check if the service is running

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Example Requests

#### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30
  }'
```

#### Get User
```bash
curl http://localhost:8080/api/users/1
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "age": 31
  }'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## Architecture

### Layer Structure
- **Routes**: Handle HTTP requests and responses
- **Service**: Business logic and orchestration
- **Repository**: Database access layer
- **Cache Service**: Redis caching layer
- **Models**: Data transfer objects and entities

### Technologies
- **Ktor**: Web framework
- **Exposed**: SQL framework
- **PostgreSQL**: Primary database
- **Redis**: Caching layer
- **HikariCP**: Connection pooling
- **Jedis**: Redis Java client
- **Koin**: Dependency injection

## Configuration

The application can be configured via environment variables:

- `DB_HOST`: PostgreSQL host (default: localhost)
- `DB_PORT`: PostgreSQL port (default: 5432)
- `DB_NAME`: Database name (default: mydatabase)
- `DB_USER`: Database user (default: myuser)
- `DB_PASSWORD`: Database password (default: mypassword)
- `REDIS_HOST`: Redis host (default: localhost)
- `REDIS_PORT`: Redis port (default: 6379)

## Testing

Run tests with:
```bash
./gradlew test
```

## Docker Deployment

To run the entire application in Docker:

1. Build the application:
```bash
./gradlew buildFatJar
```

2. Create a Dockerfile:
```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/*-all.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

3. Build and run with docker-compose

## License

MIT