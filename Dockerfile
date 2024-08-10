# Use an official opnejdk:14 runtime as a builder
FROM openjdk:14 AS builder
# Set the working directory in the container
WORKDIR /app
# Copy the application code to the working directory
COPY . .
# Authorize to execute gradle
RUN chmod +x ./gradlew
RUN microdnf install findutils
# Clean before build
RUN ./gradlew clean
# Build the .jar file
RUN ./gradlew bootJar

# Use an official opnejdk:14 runtime as a parent image
FROM openjdk:14
# Set the working directory in the container
WORKDIR /app
# Copy the .jar file to the working directory
COPY --from=builder /app/build/libs/seawater-0.0.1-SNAPSHOT.jar app.jar
# Expose the port the app runs on: prod - 8081, test - 9081
EXPOSE 8081
# Set the command to run the app: prod, test
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]