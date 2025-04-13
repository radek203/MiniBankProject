FROM amazoncorretto:21

WORKDIR /app

COPY target/CreditCardService-0.0.1-SNAPSHOT.jar /app/creditcardservice-app.jar

ENTRYPOINT ["java", "-jar", "creditcardservice-app.jar"]
