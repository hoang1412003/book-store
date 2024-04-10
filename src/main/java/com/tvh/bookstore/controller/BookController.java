package com.tvh.bookstore.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tvh.bookstore.entity.Book;
import com.tvh.bookstore.service.IBookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private IBookService bookService;

    @GetMapping
    public List<Book> getBookList() {
        return bookService.getList();
    }

    @GetMapping("/{Id}")
    public Optional<Book> getBook(@PathVariable(name = "Id") int id){
        return bookService.getBookById(id);
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable int id, @RequestBody Book bookDetails) {
        Optional<Book> optionalExistingBook = bookService.getBookById(id);
        if (optionalExistingBook.isPresent()) {
            Book existingBook = optionalExistingBook.get();
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setDescription(bookDetails.getDescription());
            existingBook.setImg(bookDetails.getImg());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setPrice(bookDetails.getPrice());
            bookService.updateBook(existingBook);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") int id) {
        Optional<Book> optionalExistingBook = bookService.getBookById(id);
        if (optionalExistingBook.isPresent()) {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    
}
