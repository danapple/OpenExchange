# Use an official Maven image as the base image
FROM jelastic/maven:3.9.5-openjdk-21 AS build
# Set the working directory in the container
WORKDIR /app

# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY ./exchange/target/openexchange-exchange-1.0-SNAPSHOT.jar /app
COPY ./application.yml /app
# Set the command to run the application
CMD ["java", "-jar", "/app/openexchange-exchange-1.0-SNAPSHOT.jar"]
