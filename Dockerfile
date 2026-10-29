# ==========================================
# Stage 1: Build Spring Boot JAR with Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven wrapper and POM descriptor
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies (leveraging layer caching)
RUN ./mvnw dependency:go-offline -B || true

# Copy project source and package the production JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ==========================================
# Stage 2: Minimal Production JRE Runtime
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as a dedicated non-privileged user for security
RUN groupadd -r tradetrack && useradd -r -g tradetrack tradetrack

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R tradetrack:tradetrack /app

USER tradetrack

# Render assigns a dynamic port via $PORT (fallback 8080)
ENV PORT=8080
EXPOSE ${PORT}

# Start the Spring Boot application binding to 0.0.0.0 and dynamic PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Dserver.address=0.0.0.0 -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
