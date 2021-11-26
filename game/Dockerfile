FROM openjdk:15-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dserver.port=13378","-Dapplication.mail=example@mail.com","-Dapplication.mail.pass=mailpass","-jar","app.jar"]