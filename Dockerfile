# Use an OpenJDK base image
FROM openjdk:8

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the compiled Java application JAR file into the container
COPY ./target/dsa360.jar .

# Define the command to run the Java application
CMD ["java", "-jar", "dsa360.jar"]
