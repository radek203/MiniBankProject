FROM amazoncorretto:21

WORKDIR /app

COPY target/HeadquarterService-0.0.1-SNAPSHOT.jar /app/headquarterservice-app.jar

ENTRYPOINT ["java", "-jar", "headquarterservice-app.jar"]
