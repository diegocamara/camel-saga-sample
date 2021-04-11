#!/bin/sh
./mvnw clean package -f orders/pom.xml
./mvnw clean package -f flight/pom.xml
./mvnw clean package -f hotel/pom.xml
./mvnw clean package -f payments/pom.xml
docker-compose up --build