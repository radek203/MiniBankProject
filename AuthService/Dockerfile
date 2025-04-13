FROM amazoncorretto:21

WORKDIR /app

COPY target/AuthService-0.0.1-SNAPSHOT.jar /app/authservice-app.jar

ENTRYPOINT ["java", "-jar", "authservice-app.jar"]
