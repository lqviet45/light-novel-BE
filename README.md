# 📚 Light Novel Platform - Microservices Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-green.svg)](#)

A scalable, modular microservices-based system for hosting, managing, reading, and recommending light novels. Built with Spring Boot, Spring Security, and designed for cloud-native deployment.

## 📋 Table of Contents

- [🚀 Quick Start](#-quick-start)
- [🏗️ Architecture](#️-architecture)
- [⚡ Current Implementation](#-current-implementation)
- [🛠️ Tech Stack](#️-tech-stack)
- [📦 Prerequisites](#-prerequisites)
- [🔧 Installation & Setup](#-installation--setup)
- [🌐 API Documentation](#-api-documentation)
- [🗂️ Project Structure](#️-project-structure)
- [🔐 Security](#-security)
- [🎯 Roadmap](#-roadmap)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

## 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/lqviet45/light-novel-BE.git
cd light-novel-BE

# Start PostgreSQL database
docker-compose up -d

# Run the user service
cd user-service
./mvnw spring-boot:run
```

Access the application at `http://localhost:8081`

## 🏗️ Architecture

### Current Architecture
```
┌─────────────────┐
│   Client Apps   │
│ (Web/Mobile)    │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│  User Service   │
│   Port: 8081    │
│ ┌─────────────┐ │
│ │    User     │ │
│ │ Management  │ │
│ └─────────────┘ │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│  PostgreSQL     │
│   Database      │
│   Port: 5432    │
└─────────────────┘
```

### Planned Microservices Architecture
```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │   Port: 8000    │
                    └─────────┬───────┘
                              │
          ┌───────────┬───────┼───────┬───────────┐
          │           │       │       │           │
          ▼           ▼       ▼       ▼           ▼
    ┌──────────┐┌──────────┐┌──────┐┌──────┐┌────────────┐
    │   User   ││   Auth   ││Novel ││Chapter││  Other     │
    │ Service  ││ Service  ││Service│Service││ Services   │
    │   8081   ││   8082   ││ 8083 ││ 8084  ││8085 - 809X │
    └──────────┘└──────────┘└──────┘└──────┘└────────────┘
          │           │       │       │           │
          └───────────┴───────┼───────┴───────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │   + MongoDB     │
                    └─────────────────┘
```

## ⚡ Current Implementation

### 🟢 Implemented Features
- **User Service**: Complete user management with CRUD operations
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **Docker Support**: Docker Compose for database
- **RESTful APIs**: Full CRUD operations for user management

### 🟡 In Development
- Auth service with JWT authentication
- Novel management service
- Chapter content service
- API Gateway implementation

### 🔴 Planned Features
- Search service with Elasticsearch
- Comment and rating system
- Notification service
- Reading history tracking
- Admin dashboard
- AI-powered recommendations
- Content crawler service
- Multi-language translation

## 🛠️ Tech Stack

### Current Implementation
| Category | Technology |
|----------|------------|
| **Backend Framework** | Spring Boot 3.5.3 |
| **Security** | Spring Security |
| **Database** | PostgreSQL |
| **ORM** | JPA/Hibernate |
| **Build Tool** | Maven |
| **Containerization** | Docker & Docker Compose |
| **Java Version** | 21+ |

### Planned Technologies
| Category | Technology |
|----------|------------|
| **Authentication** | JWT (in Auth Service) |
| **Service Discovery** | Eureka Server |
| **API Gateway** | Spring Cloud Gateway |
| **Message Queue** | Apache Kafka |
| **Caching** | Redis |
| **Search Engine** | Elasticsearch |
| **Monitoring** | Prometheus & Grafana |
| **Documentation** | OpenAPI 3.0 (Swagger) |

## 📦 Prerequisites

- **Java 17+** - [Download OpenJDK](https://openjdk.java.net/)
- **Maven 3.6+** - [Installation Guide](https://maven.apache.org/install.html)
- **Docker & Docker Compose** - [Get Docker](https://docs.docker.com/get-docker/)
- **PostgreSQL** (or use Docker Compose)

## 🔧 Installation & Setup

### 1. Clone Repository
```bash
git clone https://github.com/lqviet45/light-novel-BE.git
cd light-novel-BE
```

### 2. Start Database
```bash
# Start PostgreSQL using Docker Compose
docker-compose up -d user-service-postgres

# Verify database is running
docker-compose ps
```

### 3. Configure Application
The application uses the following default configuration:
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/noveldb
    username: admin
    password: secret
```

### 4. Build and Run
```bash
cd user-service

# Build the application
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

### 5. Verify Installation
```bash
# Check application health
curl http://localhost:8081/actuator/health

# Expected response: {"status":"UP"}
```

## 🌐 API Documentation

### User Service Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/v1/users/{id}` | Get user by ID | ✅ |
| `GET` | `/api/v1/users/email/{email}` | Get user by email | ✅ |
| `POST` | `/api/v1/users` | Create user | ❌ |
| `PUT` | `/api/v1/users/{id}` | Update user | ✅ |
| `PUT` | `/api/v1/users/{id}/password` | Update user password | ✅ |
| `DELETE` | `/api/v1/users/{id}` | Delete user | ✅ (Admin) |

### Auth Service Endpoints (Coming Soon)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/register` | Register new user |
| `POST` | `/api/v1/auth/login` | User login |
| `POST` | `/api/v1/auth/refresh` | Refresh JWT token |
| `POST` | `/api/v1/auth/logout` | Logout user |

### Request/Response Examples

#### User Registration
```json
POST /api/v1/users
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### Response
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "status": "ACTIVE",
    "roles": ["USER"],
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🗂️ Project Structure

```
light-novel-BE/
├── user-service/                 # User management service
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/lqviet/userservice/
│   │   │   │       ├── UserServiceApplication.java
│   │   │   │       ├── config/           # Configuration
│   │   │   │       ├── controller/       # REST controllers
│   │   │   │       ├── entity/          # JPA entities
│   │   │   │       ├── repository/      # Data repositories
│   │   │   │       ├── service/         # Business logic
│   │   │   │       └── dto/             # Data transfer objects
│   │   │   └── resources/
│   │   │       ├── application.yml      # Application configuration
│   │   │       └── data.sql            # Initial data (optional)
│   │   └── test/                       # Unit and integration tests
│   ├── target/                         # Build artifacts
│   └── pom.xml                        # Maven dependencies
├── auth-service/                      # Authentication service (planned)
├── novel-service/                     # Novel management service (planned)
├── chapter-service/                   # Chapter content service (planned)
├── api-gateway/                       # API Gateway (planned)
├── compose.yaml                       # Docker Compose configuration
├── README.md                         # Project documentation
└── .gitignore                       # Git ignore patterns
```

## 🔐 Security

### Authentication Flow (Planned)
1. User registers/logs in with credentials via Auth Service
2. Auth Service validates and returns JWT token
3. Client includes token in `Authorization: Bearer <token>` header
4. API Gateway validates token on each request
5. Individual services focus on their core functionality

### Security Features
- **Password Hashing**: BCrypt encryption
- **JWT Tokens**: Secure stateless authentication (coming in Auth Service)
- **Role-based Access**: USER and ADMIN roles
- **CORS Configuration**: Cross-origin request handling
- **SQL Injection Prevention**: JPA prepared statements

### Environment Variables
For production deployment, configure these environment variables:
```bash
JWT_SECRET=your-production-jwt-secret-key-here
POSTGRES_URL=jdbc:postgresql://your-db-host:5432/noveldb
POSTGRES_USERNAME=your-db-username
POSTGRES_PASSWORD=your-db-password
```

## 🎯 Roadmap

### Phase 1: Foundation (🚧 In Progress)
- [x] User Service with CRUD operations
- [ ] Auth Service with JWT authentication
- [ ] PostgreSQL database integration
- [x] Docker containerization

### Phase 2: Core Services (📋 Planned)
- [ ] Novel Service (CRUD operations)
- [ ] Chapter Service (content management)
- [ ] API Gateway setup
- [ ] Service discovery with Eureka

### Phase 3: Extended Features (📋 Planned)
- [ ] Search Service with Elasticsearch
- [ ] Comment and rating system
- [ ] Notification service
- [ ] Reading history tracking
- [ ] Admin dashboard

### Phase 4: Advanced Features (🔮 Future)
- [ ] AI-powered recommendations
- [ ] Content crawler service
- [ ] Multi-language translation
- [ ] Mobile app support
- [ ] Real-time notifications
- [ ] Analytics and monitoring

## 🤝 Contributing

We welcome contributions! Please follow these steps:

### 1. Fork & Clone
```bash
git clone https://github.com/your-username/light-novel-BE.git
cd light-novel-BE
```

### 2. Create Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 3. Development Guidelines
- Follow [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html)
- Write unit tests for new features
- Update documentation for API changes
- Ensure code passes all existing tests

### 4. Testing
```bash
cd user-service
./mvnw test
```

### 5. Submit Pull Request
1. Push your changes to your fork
2. Create a Pull Request with clear description
3. Ensure CI checks pass
4. Request review from maintainers

### Development Setup
```bash
# Install pre-commit hooks (optional)
pip install pre-commit
pre-commit install

# Run code formatting
./mvnw spring-javaformat:apply
```

### Reporting Issues
- Use [GitHub Issues](https://github.com/lqviet45/light-novel-BE/issues)
- Include detailed reproduction steps
- Provide environment information
- Add relevant logs or error messages

## 📄 License

This project is licensed under the Apache 2 License - see the [LICENSE](LICENSE) file for details.

```
Apache 2 License

Copyright (c) 2025 Lê Quốc Việt

<div align="center">

**⭐ Star this repository if it helped you!**

[Report Bug](https://github.com/lqviet45/light-novel-BE/issues) • [Request Feature](https://github.com/lqviet45/light-novel-BE/issues) • [Contribute](https://github.com/lqviet45/light-novel-BE/pulls)
