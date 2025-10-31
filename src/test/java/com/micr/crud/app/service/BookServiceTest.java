package com.micr.crud.app.service;

import com.micr.crud.app.entity.Book;
import com.micr.crud.app.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class BookServiceTest {
    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void testFindAll() {
        Book b1 = new Book(1L, "Micronaut in Action", "John Doe");
        Book b2 = new Book(2L, "Reactive Java", "Jane Doe");

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));

        List<Book> books = bookService.findAll();

        assertEquals(2, books.size());
        assertEquals("Micronaut in Action", books.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Book b = new Book(1L, "Micronaut Guide", "Alex");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));

        Optional<Book> result = bookService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Micronaut Guide", result.get().getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void testSave() {
        Book b = new Book(null, "New Book", "Author");
        Book saved = new Book(100L, "New Book", "Author");
        when(bookRepository.save(b)).thenReturn(saved);

        Book result = bookService.save(b);

        assertNotNull(result.getId());
        assertEquals(100L, result.getId());
        verify(bookRepository).save(b);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        doNothing().when(bookRepository).deleteById(id);

        bookService.delete(id);

        verify(bookRepository, times(1)).deleteById(id);
    }
}
