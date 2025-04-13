FROM amazoncorretto:21

WORKDIR /app

COPY target/BranchService-0.0.1-SNAPSHOT.jar /app/branchservice-app.jar

ENTRYPOINT ["java", "-jar", "branchservice-app.jar"]
