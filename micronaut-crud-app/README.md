# ⚙️ Micronaut CRUD App

This module is a **Micronaut 4.10.1** web application for managing `Book` entities.

---

## 📦 Tech Stack

- **Micronaut 4.10.1**
- **Micronaut Data (Hibernate JPA)**
- **H2 in-memory database**
- **Micronaut Serde (Jackson)**
- **Lombok**
- **JUnit 5 + Micronaut Test**

---

## 🚀 Run Application

```bash
mvn mn:run
```

or

```java
java - jar target/micronaut-crud-app-1.0-SNAPSHOT.jar
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
curl -X POST http://localhost:8081/books \
-H "Content-Type: application/json" \
-d '{"title": "Reactive Micronaut", "author": "Jane"}'
```

- Integration tests are in src/test/java/.../integration.
