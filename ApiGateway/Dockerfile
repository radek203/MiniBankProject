FROM amazoncorretto:21

WORKDIR /app

COPY target/ApiGateway-0.0.1-SNAPSHOT.jar /app/apigateway-app.jar

ENTRYPOINT ["java", "-jar", "apigateway-app.jar"]
