package com.micr.crud.app.integration.controller;

import com.micr.crud.app.entity.Book;
import com.micr.crud.app.repository.BookRepository;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(transactional = false)
public class BookControllerIntegrationTest {
    @Inject
    @Client("/books")
    HttpClient client;

    @Inject
    BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        bookRepository.save(new Book(null, "Micronaut in Action", "John Doe"));
        bookRepository.save(new Book(null, "Reactive Java", "Jane Doe"));
    }

    @Test
    void testListBooks() {
        HttpResponse<List<Book>> response = client.toBlocking().exchange(HttpRequest.GET("/"), Argument.listOf(Book.class));

        assertEquals(200, response.getStatus().getCode());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());
    }

    @Test
    void testGetBookById() {
        Book book = bookRepository.findAll().iterator().next();
        HttpResponse<Book> response = client.toBlocking().exchange(HttpRequest.GET("/" + book.getId()), Book.class);

        assertEquals(200, response.getStatus().getCode());
        assertEquals(book.getTitle(), response.body().getTitle());
    }

    @Test
    void testCreateBook() {
        Book newBook = new Book(null, "New Micronaut Book", "Alice");

        HttpResponse<Book> response =
                client.toBlocking().exchange(HttpRequest.POST("/", newBook), Book.class);

        assertEquals(201, response.getStatus().getCode());
        Book createdBook = response.body();
        assertNotNull(createdBook);
        assertEquals("New Micronaut Book", createdBook.getTitle());
    }

    @Test
    void testDeleteBook() {
        Book book = bookRepository.save(new Book(null, "Temp Book", "Bob"));

        HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.DELETE("/" + book.getId()));

        assertEquals(204, response.getStatus().getCode());
        assertFalse(bookRepository.findById(book.getId()).isPresent());
    }
}
