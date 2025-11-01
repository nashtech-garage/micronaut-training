# ⚙️ Spring Boot CRUD App

This module is a **Spring Boot 3.3.4** web application implementing the same CRUD functionality as the Micronaut app.

---

## 📦 Tech Stack

- **Spring Boot 3.3.4**
- **Spring Web + Spring Data JPA**
- **H2 in-memory database**
- **Lombok**
- **JUnit 5 + Spring Boot Test + MockMvc**

---

## 🚀 Run Application

```bash
mvn spring-boot:run
```

or

```java
java -jar target/springboot-crud-app-1.0-SNAPSHOT.jar
```

---

## 🧪 Test

```bash
mvn test
```

---

## 📚 REST Endpoints

| Method   | Endpoint      | Description     |
|----------|---------------|-----------------|
| `GET`    | `/books`      | List all books  |
| `GET`    | `/books/{id}` | Get book by ID  |
| `POST`   | `/books`      | Create new book |
| `DELETE` | `/books/{id}` | Delete book     |

Example JSON:

```json
{
  "title": "Micronaut in Action",
  "author": "John Doe"
}

```

---

## 🧠 Notes

- To test endpoints, use Postman or curl:

```json
curl -X POST http://localhost:8080/books \
-H "Content-Type: application/json" \
-d '{"title": "Reactive Micronaut", "author": "Jane"}'
```

- Integration tests are in src/test/java/.../integration.
