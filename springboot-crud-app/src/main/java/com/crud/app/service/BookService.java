package com.crud.app.service;

import com.crud.app.entity.Book;
import com.crud.app.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
