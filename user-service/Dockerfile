# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests
# Optional: Uncomment for debugging
# RUN ls -la /app/target/

# Run stage
FROM openjdk:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/*.war ./app.war

EXPOSE 8000

CMD ["java", "-jar", "app.war"]