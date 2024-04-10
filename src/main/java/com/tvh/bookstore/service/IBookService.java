package com.tvh.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.tvh.bookstore.entity.Book;

public interface IBookService {
    List<Book> getList();
    Optional<Book> getBookById(int id);
    Book addBook(Book book);
    void updateBook(Book book);
    void deleteBook(int id);
}
