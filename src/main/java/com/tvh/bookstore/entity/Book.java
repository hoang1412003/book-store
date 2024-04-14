package com.tvh.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_book")
    private int idBook;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name="img", columnDefinition="BLOB")
    private byte[] img;
    @Column(name = "price")
    private double price;
    @Column(name = "author")
    private String author;
    
    public Book() {
    }
    
    

    public Book(int idBook, String title, String description, byte[] img, double price, String author) {
        this.idBook = idBook;
        this.title = title;
        this.description = description;
        this.img = img;
        this.price = price;
        this.author = author;
    }



    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }



    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    
}
