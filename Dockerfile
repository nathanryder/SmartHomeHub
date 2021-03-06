FROM maven:3.5-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN sed -i "s/127.0.0.1/host.docker.internal/g" /usr/src/app/src/main/resources/application.properties
RUN mvn -f /usr/src/app/pom.xml package

FROM openjdk:11-jre-slim
COPY --from=build /usr/src/app/target/finalyearproject-*-SNAPSHOT.jar /usr/app/finalyearproject.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/finalyearproject.jar"]