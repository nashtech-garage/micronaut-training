package com.crud.app.controller;

import com.crud.app.entity.Book;
import com.crud.app.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ( value = "/books")
@CrossOrigin(origins = "*")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @RequestMapping ( method = RequestMethod.GET )
    public List<Book> list() {
        return service.findAll();
    }

    @RequestMapping (method = RequestMethod.GET , value = "/{id}" )
    public ResponseEntity<Book> get(@PathVariable("id") Long id) {
        Optional<Book> book = service.findById(id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = service.save(book);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
