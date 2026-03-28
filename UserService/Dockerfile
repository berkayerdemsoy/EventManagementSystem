# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1) user-service-client build & install
COPY user-service-client/pom.xml ./user-service-client/pom.xml
WORKDIR /app/user-service-client
RUN mvn dependency:go-offline -B
COPY user-service-client/src ./src
RUN mvn clean install -DskipTests -B

# 2) user-service-app build
WORKDIR /app/user-service-app
COPY user-service-app/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY user-service-app/src ./src
RUN mvn clean package -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/user-service-app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

