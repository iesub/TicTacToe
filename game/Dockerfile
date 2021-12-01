FROM openjdk:15-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dserver.ip=localhost","-Dserver.port=90","-Dapplication.mail=example@mail.com","-Dapplication.mail.pass=pass","-jar","app.jar"]
