# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /usr/src/app
COPY pom.xml .
# Download dependencies first (makes future builds faster)
RUN mvn dependency:go-offline
COPY src ./src
# Compile the Quarkus app
RUN mvn clean package -DskipTests

# Stage 2: Create the lightweight production runner
FROM eclipse-temurin:17-jre
WORKDIR /deployments

# Copy the compiled Quarkus fast-jar files from Stage 1
COPY --from=build /usr/src/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /usr/src/app/target/quarkus-app/*.jar /deployments/
COPY --from=build /usr/src/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /usr/src/app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Open the port Render expects
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "/deployments/quarkus-run.jar"]
