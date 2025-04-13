FROM amazoncorretto:21

WORKDIR /app

COPY target/ServiceRegistry-0.0.1-SNAPSHOT.jar /app/serviceregistry-app.jar

ENTRYPOINT ["java", "-jar", "serviceregistry-app.jar"]
