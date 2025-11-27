package com.micr.crud.app.service;

import com.micr.crud.app.annotation.RetryOnFailure;
import com.micr.crud.app.entity.Book;
import com.micr.crud.app.repository.BookRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class BookService {
    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<Book> findAll() {
        return (List<Book>) repo.findAll();
    }

    public Optional<Book> findById(Long id) {
        return repo.findById(id);
    }

    public Book save(Book book) {
        return repo.save(book);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    @RetryOnFailure(maxAttempts = 3, delayMs = 100)
    public Book saveWithRetry(Book book) {
        return repo.save(book);
    }

    // Counter for testing retry behavior
    private int failureCount = 0;

    @RetryOnFailure(maxAttempts = 3, delayMs = 100)
    public Book saveWithSimulatedRetry(Book book) {
        failureCount++;
        if (failureCount < 3) {
            throw new RuntimeException("Simulated temporary failure - attempt " + failureCount);
        }
        failureCount = 0; // Reset for next test
        return repo.save(book);
    }
}