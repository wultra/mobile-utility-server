# Docker Image for Build
FROM maven:3.9.6-ibm-semeru-21-jammy
WORKDIR /workspace

COPY pom.xml .
COPY lombok.config .
COPY src src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests