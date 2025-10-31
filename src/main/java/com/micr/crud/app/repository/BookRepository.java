package com.micr.crud.app.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import com.micr.crud.app.entity.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
}
