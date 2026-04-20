FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# GitHub Packages auth — ems-common buradan çekiliyor
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
RUN mkdir -p /root/.m2 && printf '<settings>\n  <servers>\n    <server>\n      <id>github</id>\n      <username>%s</username>\n      <password>%s</password>\n    </server>\n  </servers>\n</settings>' "$GITHUB_ACTOR" "$GITHUB_TOKEN" > /root/.m2/settings.xml

COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]