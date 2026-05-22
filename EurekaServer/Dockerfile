# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Bağımlılıkları önce indir (layer cache için)
#RUN mvn dependency:go-offline -B
RUN mvn dependency:go-offline -B -Dhttp.keepAlive=false -Dmaven.wagon.http.retryHandler.count=3 || \
               (echo "Ağ hatası alındı, 5 saniye bekleniyor..." && sleep 5 && mvn dependency:go-offline -B) || \
               (echo "İkinci deneme başarısız, 10 saniye bekleniyor..." && sleep 10 && mvn dependency:go-offline -B)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
