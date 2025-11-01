package com.micr.crud.app.service;

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
}
