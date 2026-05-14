# syntax=docker/dockerfile:1
# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# GitHub Packages auth — BuildKit secret mount: token hiçbir image layer'ına yazılmaz.
# Build: docker buildx build --secret id=github_token,env=GITHUB_TOKEN
#                             --secret id=github_actor,env=GITHUB_ACTOR ...
RUN --mount=type=secret,id=github_token \
    --mount=type=secret,id=github_actor \
    mkdir -p /root/.m2 && \
    printf '<settings>\n  <servers>\n    <server>\n      <id>github</id>\n      <username>%s</username>\n      <password>%s</password>\n    </server>\n  </servers>\n</settings>' \
    "$(cat /run/secrets/github_actor)" "$(cat /run/secrets/github_token)" \
    > /root/.m2/settings.xml

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
