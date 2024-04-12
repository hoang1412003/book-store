package com.tvh.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tvh.bookstore.entity.Book;

public interface IBookRepository extends JpaRepository<Book, Integer> {

}
