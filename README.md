# MiniBankProject

As a project, a system was developed to simulate a distributed banking system with a central customer database and bank branches,
each having its own local database where banking transactions are also recorded.
The goal of the project was to develop a library that enables communication with distributed SQL Server databases and manages transactions.
An additional assumption was that the databases are unaware of each other; to make things more challenging, all distributed processing is implemented using microservices.

## Technologies used

- Java 21
- Spring Boot 3
- MS SQL
- Apache Kafka
- Docker
- Angular 19

## How to run

You need to have Java 21, Maven, Docker installed on your machine.

Build the project using Maven:

```bash
  mvn clean package
```

Run docker compose:

```bash
  docker compose up
```

To run frontend (in the easiest way) navigate to `MiniBankWEB` directory:

```bash
  cd MiniBankWEB
```

Then install dependencies and run the application:

```bash
  npm install -g @angular/cli
  npm install
  ng serve
```

Then open your browser and go to `http://localhost:4200`.