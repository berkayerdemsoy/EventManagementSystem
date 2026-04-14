# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# GitHub Packages auth
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
RUN mkdir -p /root/.m2 && printf '<settings>\n  <servers>\n    <server>\n      <id>github</id>\n      <username>%s</username>\n      <password>%s</password>\n    </server>\n  </servers>\n</settings>' "$GITHUB_ACTOR" "$GITHUB_TOKEN" > /root/.m2/settings.xml

# 1) user-service-client build & install (event-service-app depends on it)
COPY UserService/user-service-client/pom.xml ./user-service-client/pom.xml
WORKDIR /app/user-service-client
RUN mvn dependency:go-offline -B
COPY UserService/user-service-client/src ./src
RUN mvn clean install -DskipTests -B

# 2) event-service-client build & install
WORKDIR /app/event-service-client
COPY event-service/event-service-client/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY event-service/event-service-client/src ./src
RUN mvn clean install -DskipTests -B

# 3) event-service-app build
WORKDIR /app/event-service-app
COPY event-service/event-service-app/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY event-service/event-service-app/src ./src
RUN mvn clean package -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/event-service-app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
