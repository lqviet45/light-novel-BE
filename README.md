📚 Novel Platform – Microservices Architecture
A scalable, modular microservices-based system for hosting, managing, reading, and recommending novels. Built with Spring Boot, Spring Cloud, Kafka, Consul, and optional AI services.

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
API Gateway (Spring Cloud Gateway)


graphql-gateway
8001
GraphQL Aggregator for mobile


consul-server
8500
Service Discovery (Consul)


config-server
8888
Centralized Config (Spring Cloud Config)



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
JWT, Role-based access


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
1. Clone repo
git clone https://github.com/your-org/novel-platform.git
cd novel-platform

2. Start core services (user, novel, chapter)
docker-compose up -d user-service novel-service chapter-service consul-server gateway-service postgres

3. Access Consul dashboard

URL: http://localhost:8500
Note: Configure Consul ACL for secure access (see Consul Security).

⚙️ Folder Structure
novel-platform/
├── user-service/
├── novel-service/
├── chapter-service/
├── search-service/
├── gateway-service/
├── graphql-gateway/
├── consul-server/
├── config-server/
├── docker-compose.yml
└── README.md


📌 Roadmap

Build user-service with JWT auth.
Connect services to Consul for service discovery.
Add novel-service + CRUD + auth check.
Add chapter-service + MongoDB support.
Setup graphql-gateway for mobile.
Add crawler-service + ai-translate-service via FastAPI.
Add search-service and integrate with Elasticsearch.
Add Redis, Kafka for caching and messaging.
Add Docker Compose + Monitoring with Prometheus/Grafana.


🔐 Authentication

JWT is used across services.
Roles: USER, ADMIN.
Secure routes via Spring Security configuration.

Consul Security

Enable Consul ACL (Access Control List) to secure service registration and discovery.
Use HTTPS for Consul server communication.
Configure service tokens in application.yml for secure integration with Spring Cloud Consul.

Example Consul configuration for a service:
spring:
  cloud:
    consul:
      host: consul-server
      port: 8500
      discovery:
        prefer-ip-address: true
        service-name: ${spring.application.name}
      config:
        enabled: true


🤝 Contributing
Feel free to fork this project, submit PRs, or request features. This project is built with a community-driven architecture in mind.

📄 License
MIT License
