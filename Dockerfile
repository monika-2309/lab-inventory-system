# Multi-stage Dockerfile for Lab Inventory Management System
# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY backend/pom.xml .
COPY backend/src ./src

# Build production jar skipping tests
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create uploads directory
RUN mkdir -p uploads

# Copy packaged jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Cloud hosting port
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
