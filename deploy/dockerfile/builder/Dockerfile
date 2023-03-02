# Docker Image for Build
FROM maven:3.9.0-ibm-semeru-17-focal
WORKDIR /workspace

COPY pom.xml .
COPY lombok.config .
COPY src src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests