FROM openjdk:8-jdk-alpine
ARG VERSION=0.0.1
ARG APP_NAME=login

#add target file location
ADD target/${APP_NAME}-${VERSION}-SNAPSHOT.jar ${APP_NAME}.jar
ENTRYPOINT ["java","-jar","/login.jar"]