# 🧩 Micronaut Training Project

This repository demonstrates a **multi-module Maven setup** that includes:

- A **Micronaut 4.10.1** CRUD application
- A **Spring Boot 3.3.4** CRUD application
- Both built on **Java 21**

---

## 🚀 How to Build

You can build the whole project at once from the root directory:

```bash
mvn clean install
```

This will build both modules (micronaut-crud-app and springboot-crud-app).

---

## ⚡ Run Individual Modules

### Run Micronaut Module

```bash
cd micronaut-crud-app
mvn mn:run
```

Or:

```java
java -
jar target/micronaut-crud-app-1.0-SNAPSHOT.jar
```

### Run Spring Boot Module

```bash
cd springboot-crud-app
mvn spring-boot:run

```

Or:

```java
java -
jar target/springboot-crud-app-1.0-SNAPSHOT.jar
```

---

## 🧪 Running Tests

Each module contains both unit tests and integration tests.

To run all tests:

```bash
mvn test
```

---

## 🧰 Requirements

- Java 21
- Maven 3.9+
- IntelliJ IDEA (recommended)

---

## 🎯 Purpose

### This project was designed for training and demonstration:

- Understand structural and runtime differences between Micronaut and Spring Boot
- Explore dependency injection, HTTP layer, data persistence, and testing patterns
- Compare startup time, dependency graph, and test simplicity

---

## 📚 Modules Summary

| Module                | Framework         | Port   | Description                                        |
|-----------------------|-------------------|--------|----------------------------------------------------|
| `micronaut-crud-app`  | Micronaut 4.10.1  | `8081` | Fast, lightweight microservice for CRUD operations |
| `springboot-crud-app` | Spring Boot 3.3.4 | `8082` | Traditional microservice built with Spring stack   |

---

## 👨‍💻 Author

Created by Hoang Le Quoc Anh – for training on Cloud-Native Microservices with Micronaut & Spring Boot.



