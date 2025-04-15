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
  npm install
  ng serve
```

Then open your browser and go to `http://localhost:4200`.