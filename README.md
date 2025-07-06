📚 Novel Platform – Microservices Architecture
A scalable, modular microservices-based system for hosting, managing, reading, and recommending novels. Built with Spring Boot, Spring Cloud, Kafka, Consul, and Keycloak for modern authentication.

🧱 Architecture Overview
                            +-------------------+
                            |   Web Frontend    |
                            | (REST via Gateway)|
                            +-------------------+
                                     |
                                     v
                          +----------------------+
                          | Spring Cloud Gateway |
                          +----------------------+
                            /         |         \
                           /          |          \
                +---------+    +-----+-----+    +------------+
                | UserSvc |    | NovelSvc |    | ChapterSvc |
                +---------+    +-----------+    +------------+
                    \             |             /
                     \            v            /
                      \     +-----------+     /
                       +--> | SearchSvc | <---+
                            +-----------+
                            +----------------+
                            | GraphQL Gateway|
                            | (for Mobile)   |
                            +----------------+
                    ...other services like Crawler, Translate...


🧩 Services List
1. Core Services



Service
Port
Description



user-service
8081
Authentication, JWT, user roles


novel-service
8082
CRUD novel, tags, genres


chapter-service
8083
Manage chapter content


2. Extended Services



Service
Port
Description



search-service
8084
Full-text search with Elasticsearch


comment-service
8085
Commenting, report, like


notification-service
8086
Email/push notification


reading-history-service
8087
Save user reading progress


admin-service
8088
Admin dashboard for moderation


3. AI / Automation Services



Service
Description



crawler-service
Auto-fetch content from external novel sites


ai-translate-service
Translate chapters using LLMs (Gemini, GPT)


recommendation-service
Recommend novels using ML/AI


4. Infrastructure



Service
Port
Description



gateway-service
8000
API Gateway (Spring Cloud Gateway) - Planned


graphql-gateway
8001
GraphQL Aggregator for mobile - Planned


consul
8500
Service Discovery (Consul) ✅


keycloak
8080
Identity Provider (Keycloak) ✅


config-server
8888
Centralized Config (Spring Cloud Config) - Planned



🛠 Tech Stack



Category
Tech



Backend
Spring Boot, Spring Cloud, Spring Security


Service Discovery
HashiCorp Consul


API Gateway
Spring Cloud Gateway


Messaging
Kafka / RabbitMQ


Auth
Keycloak OAuth2/OIDC, Role-based access (USER, ADMIN)


Database
PostgreSQL, MongoDB


Cache
Redis


Search
Elasticsearch


AI/ML
Python + Gemini/GPT + FastAPI


Monitoring
Prometheus, Grafana, ELK or Loki


Containerization
Docker + Docker Compose



🚀 Getting Started (Local)

## Prerequisites
- Docker and Docker Compose
- Java 17+ (for local development)
- Maven 3.6+ (for local development)

## Quick Start
1. Clone repo
```bash
git clone https://github.com/lqviet45/light-novel-BE.git
cd light-novel-BE
```

2. Start infrastructure services
```bash
docker-compose up -d consul keycloak keycloak-postgres user-service-postgres
```

3. Configure Keycloak realm (first time setup)
- Access Keycloak: http://localhost:8080
- Login with admin/admin
- Create realm: `light-novel`
- Create roles: `USER`, `ADMIN`
- Create users and assign roles

4. Start application services
```bash
docker-compose up -d user-service
```

5. Access services
- Consul UI: http://localhost:8500
- Keycloak Admin: http://localhost:8080
- User Service: http://localhost:8081/actuator/health

## Development Setup
```bash
cd user-service
./mvnw spring-boot:run
```

⚙️ Folder Structure
light-novel-BE/
├── user-service/          # Authentication & User Management
├── compose.yaml           # Docker Compose configuration
└── README.md

📌 Roadmap

✅ **Phase 1: Foundation** (Current)
- [x] Build user-service with Keycloak OAuth2 auth
- [x] Connect services to Consul for service discovery
- [x] Replace deprecated Eureka with modern stack

🚧 **Phase 2: Core Services**
- [ ] Add novel-service + CRUD + auth check
- [ ] Add chapter-service + MongoDB support
- [ ] Setup gateway-service for API routing

🔮 **Phase 3: Extended Features**
- [ ] Add search-service and integrate with Elasticsearch
- [ ] Add Redis, Kafka for caching and messaging
- [ ] Setup graphql-gateway for mobile
- [ ] Add crawler-service + ai-translate-service via FastAPI
- [ ] Add Docker Compose + Monitoring with Prometheus/Grafana


🔐 Authentication & Security

## Keycloak Integration
- **Identity Provider**: Keycloak OAuth2/OIDC
- **Roles**: USER, ADMIN mapped from Keycloak realm roles
- **Token Validation**: JWT tokens validated via OAuth2 Resource Server
- **Endpoints**: 
  - Public: `/actuator/**`, `/api/auth/**`
  - Protected: All other endpoints require authentication
  - Admin: `/api/admin/**` requires ADMIN role

## Consul Security
- Enable Consul ACL (Access Control List) to secure service registration and discovery
- Use HTTPS for Consul server communication in production
- Configure service tokens in application.yml for secure integration

## Service Configuration Example
```yaml
spring:
  cloud:
    consul:
      host: consul
      port: 8500
      discovery:
        prefer-ip-address: true
        service-name: ${spring.application.name}
        health-check-path: /actuator/health
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8080/realms/light-novel
```


🤝 Contributing
Feel free to fork this project, submit PRs, or request features. This project is built with a community-driven architecture in mind.

📄 License
MIT License
