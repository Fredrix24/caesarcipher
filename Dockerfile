FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

CMD ["sh", "-c", "mvn clean verify sonar:sonar -Dsonar.java.binaries=target/classes"]