FROM eclipse-temurin:17.0.5_8-jre-focal as builder
##FROM openjdk:12.0.2
## ADD extracts the tar
ADD ./build/distributions/cargoservice-1.0.tar /
WORKDIR /cargoservice-1.0/bin
COPY ./*.pl ./
COPY ./*.json ./
COPY ./*.bin ./
COPY ./*.txt ./
ENV MONGO_HOST=mongodb  
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka/
ENV SERVICE_IP=192.168.1.132
ENV SERVICE_URL=http://192.168.1.132:8111
RUN mkdir logs

CMD ["bash", "cargoserviceqak"]


## 1) gradlew distTar
## 2) docker build -t cargoserviceqak:1.0 .
## 3) docker tag cargoserviceqak:1.0 natbodocker/cargoserviceqak:1.0
#  docker login --username natbodocker --password xyz
#  docker push natbodocker/cargoserviceqak:1.0
## docker run -it --rm --name cargoserviceqak -p8111:8111/tcp -p8111:8111/udp --privileged cargoserviceqak:1.0  /bin/bash
## docker run -it --rm --name cargoserviceqak -p8111:8111/tcp -p8111:8111/udp \
## --env MONGO_URL=mongodb://mongosrv:27017 \
## --env EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka/  \
## --privileged cargoserviceqak:1.0  /bin/bash

## --env-file my_app.env

## le variabili di ambiente possono anche essere definite direttamente 
## nell'immagine Docker usando l'istruzione ENV nel Dockerfile.
## OCCORRE APRIRE UDP PER PERMETTERE COAP
 