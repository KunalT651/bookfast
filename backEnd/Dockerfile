# Use Maven with OpenJDK 21 for building
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use lightweight JRE for runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render uses PORT env variable)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
