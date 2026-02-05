


# -------- Stage 1: Build the JAR --------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

# Copy Maven wrapper and project files
COPY src ./src

# Build the fat JAR using Spring Boot plugin
RUN mvn clean package spring-boot:repackage -DskipTests

# -------- Stage 2: Run the app --------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

