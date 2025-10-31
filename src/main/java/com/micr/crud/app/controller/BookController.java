package com.micr.crud.app.controller;

import com.micr.crud.app.entity.Book;
import com.micr.crud.app.service.BookService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Get
    public List<Book> list() {
        return service.findAll();
    }

    @Get("/{id}")
    public HttpResponse<Book> get(Long id) {
        Optional<Book> book = service.findById(id);
        return book.map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Post
    public HttpResponse<Book> create(@Body Book book) {
        Book saved = service.save(book);
        return HttpResponse.created(saved);
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(Long id) {
        service.delete(id);
        return HttpResponse.noContent();
    }
}