# E-Ship Application

A modern e-commerce shipping application built with Spring Boot 3.5.10.

## Technology Stack

- **Spring Boot**: 3.5.10 (Latest version)
- **Java**: 17
- **Build Tool**: Maven
- **Web Framework**: Spring Web MVC

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Getting Started

### Build the Application

```bash
mvn clean package
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Run Tests

```bash
mvn test
```

## API Endpoints

- `GET /api/hello` - Welcome message
- `GET /api/version` - Spring Boot version information

## Project Structure

```
eship/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/thewu/eship/
│   │   │       ├── EshipApplication.java
│   │   │       └── controller/
│   │   │           └── HomeController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/thewu/eship/
│               └── EshipApplicationTests.java
└── pom.xml
```