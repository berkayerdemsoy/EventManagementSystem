#!/bin/bash

echo "EMS Mikroservisleri Derleniyor ve Optimize Ediliyor..."

# --- EurekaServer ---
echo "---------------------------------------------------"
echo "İşleniyor: eurekaserver"
docker build -t eurekaserver:latest ./EurekaServer
slim build --http-probe=true --tag eurekaserver:latest --target eurekaserver:latest

# --- API Gateway ---
echo "---------------------------------------------------"
echo "İşleniyor: api-gateway"
docker build -t api-gateway:latest ./api-gateway
slim build --http-probe=true --tag api-gateway:latest --target api-gateway:latest

# --- User Service ---
echo "---------------------------------------------------"
echo "İşleniyor: user-service"
docker build -t user-service:latest \
  --build-arg GITHUB_ACTOR=${GITHUB_ACTOR} \
  --build-arg GITHUB_TOKEN=${GITHUB_TOKEN} \
  ./UserService
slim build --http-probe=true --tag user-service:latest --target user-service:latest

# --- Event Service ---
echo "---------------------------------------------------"
echo "İşleniyor: event-service"
docker build -t event-service:latest \
  -f event-service/Dockerfile \
  --build-arg GITHUB_ACTOR=${GITHUB_ACTOR} \
  --build-arg GITHUB_TOKEN=${GITHUB_TOKEN} \
  .
slim build --http-probe=true --tag event-service:latest --target event-service:latest

echo "---------------------------------------------------"
echo "Tüm servisler başarıyla küçültüldü!"
echo "Docker Compose ile ayağa kaldırılıyor..."

docker-compose up -d --no-build