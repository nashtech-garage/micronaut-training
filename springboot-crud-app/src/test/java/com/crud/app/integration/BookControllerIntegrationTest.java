package com.crud.app.integration;

import com.crud.app.entity.Book;
import com.crud.app.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository repo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repo.deleteAll();
        repo.save(new Book(null, "Spring Boot in Action", "John"));
        repo.save(new Book(null, "Java 21 Basics", "Jane"));
    }

    @Test
    void testListBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetBookById() throws Exception {
        Book savedBook = repo.save(new Book(null, "Clean Architecture", "Robert C. Martin"));
        // Act
        MvcResult result = mockMvc.perform(get("/books/{id}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn(); // 👉 lấy full response trả về

        // Parse JSON → Object
        String jsonResponse = result.getResponse().getContentAsString();
        Book responseBook = objectMapper.readValue(jsonResponse, Book.class);

        // Assert
        assertNotNull(responseBook);
        assertEquals(savedBook.getId(), responseBook.getId());
        assertEquals(savedBook.getTitle(), responseBook.getTitle());
        assertEquals(savedBook.getAuthor(), responseBook.getAuthor());
    }

    @Test
    void testCreateBook() throws Exception {
        String json = """
                {"title": "New Book", "author": "Alice"}
                """;

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void testDeleteBook() throws Exception {
        Book book = repo.save(new Book(null, "Temp", "Bob"));

        mockMvc.perform(delete("/books/" + book.getId()))
                .andExpect(status().isNoContent());
    }
}
