# Stage 1: Build avec Maven et tests activés
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app

# Copier pom et sources
COPY pom.xml .
COPY src ./src

# Build complet avec tests
RUN mvn clean package -DskipTests=false

# Stage 2: Runtime léger
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copier le jar produit
COPY --from=builder /app/target/aggregator-app.jar app.jar

EXPOSE 8077

ENTRYPOINT ["java", "-jar", "app.jar"]
