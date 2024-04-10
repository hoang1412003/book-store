package com.tvh.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tvh.bookstore.entity.Book;

@Repository
public interface IBookRepository extends JpaRepository<Book, Integer> {

}
