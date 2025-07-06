# 📚 Light Novel Platform - Microservices Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
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
│ │   Spring    │ │
│ │  Security   │ │
│ └─────────────┘ │
│ ┌─────────────┐ │
│ │     JWT     │ │
│ │    Auth     │ │
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
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
    ┌──────────┐        ┌──────────┐        ┌──────────┐
    │   User   │        │  Novel   │        │ Chapter  │
    │ Service  │        │ Service  │        │ Service  │
    │   8081   │        │   8082   │        │   8083   │
    └──────────┘        └──────────┘        └──────────┘
          │                   │                   │
          └───────────────────┼───────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │   + MongoDB     │
                    └─────────────────┘
```

## ⚡ Current Implementation

### 🟢 Implemented Features
- **User Service**: Complete user management with JWT authentication
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **Security**: Spring Security with JWT tokens
- **Role-based Access**: USER and ADMIN roles
- **Docker Support**: Docker Compose for database
- **RESTful APIs**: Full CRUD operations for user management

### 🟡 In Development
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
| **Security** | Spring Security + JWT |
| **Database** | PostgreSQL |
| **ORM** | JPA/Hibernate |
| **Build Tool** | Maven |
| **Containerization** | Docker & Docker Compose |
| **Java Version** | 17+ |

### Planned Technologies
| Category | Technology |
|----------|------------|
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

### Authentication Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register new user |
| `POST` | `/api/auth/login` | User login |
| `POST` | `/api/auth/refresh` | Refresh JWT token |

### User Management Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/users/profile` | Get user profile | ✅ |
| `PUT` | `/api/users/profile` | Update profile | ✅ |
| `GET` | `/api/users` | List all users | ✅ (Admin) |
| `DELETE` | `/api/users/{id}` | Delete user | ✅ (Admin) |

### Request/Response Examples

#### User Registration
```json
POST /api/auth/register
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
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-15T10:30:00Z"
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
│   │   │   │       ├── config/           # Security & JWT config
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
├── compose.yaml                       # Docker Compose configuration
├── README.md                         # Project documentation
└── .gitignore                       # Git ignore patterns
```

## 🔐 Security

### Authentication Flow
1. User registers/logs in with credentials
2. Server validates and returns JWT token
3. Client includes token in `Authorization: Bearer <token>` header
4. Server validates token on each request

### Security Features
- **Password Hashing**: BCrypt encryption
- **JWT Tokens**: Secure stateless authentication
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

### Phase 1: Foundation (✅ Current)
- [x] User Service with JWT authentication
- [x] PostgreSQL database integration
- [x] Basic CRUD operations
- [x] Docker containerization

### Phase 2: Core Services (🚧 In Progress)
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

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 lqviet45

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

**⭐ Star this repository if it helped you!**

[Report Bug](https://github.com/lqviet45/light-novel-BE/issues) • [Request Feature](https://github.com/lqviet45/light-novel-BE/issues) • [Contribute](https://github.com/lqviet45/light-novel-BE/pulls)

</div>

