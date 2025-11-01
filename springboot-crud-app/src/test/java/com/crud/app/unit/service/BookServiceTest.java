package com.crud.app.unit.service;

import com.crud.app.entity.Book;
import com.crud.app.repository.BookRepository;
import com.crud.app.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private BookRepository repo;
    private BookService service;

    @BeforeEach
    void setUp() {
        repo = mock(BookRepository.class);
        service = new BookService(repo);
    }

    @Test
    void testFindAll() {
        // given
        var books = List.of(
                new Book(1L, "Spring Boot in Action", "Craig Walls"),
                new Book(2L, "Effective Java", "Joshua Bloch")
        );
        when(repo.findAll()).thenReturn(books);

        // when
        var result = service.findAll();

        // then
        assertEquals(2, result.size());
        assertEquals("Effective Java", result.get(1).getTitle());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testFindById() {
        var book = new Book(1L, "Clean Code", "Robert C. Martin");
        when(repo.findById(1L)).thenReturn(Optional.of(book));

        var result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Clean Code", result.get().getTitle());
        verify(repo).findById(1L);
    }

    @Test
    void testSave() {
        var book = new Book(null, "Domain-Driven Design", "Eric Evans");
        var saved = new Book(10L, "Domain-Driven Design", "Eric Evans");

        when(repo.save(book)).thenReturn(saved);

        var result = service.save(book);

        assertNotNull(result.getId());
        assertEquals(10L, result.getId());
        verify(repo, times(1)).save(book);
    }

    @Test
    void testDelete() {
        long id = 5L;

        // when
        service.delete(id);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(repo, times(1)).deleteById(captor.capture());
        assertEquals(id, captor.getValue());
    }
}
