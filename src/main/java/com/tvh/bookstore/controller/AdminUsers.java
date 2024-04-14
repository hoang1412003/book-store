package com.tvh.bookstore.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tvh.bookstore.dto.ReqRes;
import com.tvh.bookstore.entity.Book;
import com.tvh.bookstore.service.IBookService;

import io.jsonwebtoken.io.IOException;

@RestController
// @RequestMapping("/api")
public class AdminUsers {
    @Autowired
    IBookService bookService;

    // API chỉ dành cho người dùng
    @GetMapping("/user/alone")
    public ResponseEntity<Object>userAlone(){
        return ResponseEntity.ok("USers alone can access this ApI only");
    }

    @PostMapping("/admin/savebook")
    public ResponseEntity<Object> saveBook(@RequestBody Book bookRequest) throws IOException {
    // Xử lý tệp hình ảnh từ imgFile, chuyển thành byte[]
    byte[] imgData = bookRequest.getImg();
    // Tạo đối tượng Book từ dữ liệu nhận được từ yêu cầu
    Book book = new Book();
    book.setTitle(bookRequest.getTitle());
    book.setDescription(bookRequest.getDescription());
    book.setImg(imgData);
    book.setPrice(bookRequest.getPrice());
    book.setAuthor(bookRequest.getAuthor());
    
    // Thêm sách vào cơ sở dữ liệu và trả về phản hồi
    Book savedBook = bookService.addBook(book);
    return ResponseEntity.ok(savedBook);
}

    
    // API có thể truy cập bởi cả quản trị viên và người dùng
    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminaAndUsersApi(){
        return ResponseEntity.ok("Both Admin and Users Can access the api");
    }
    
    // Lấy địa chỉ email của người dùng hiện tại
    @GetMapping("/public/email")
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication); 
        System.out.println(authentication.getDetails()); 
        System.out.println(authentication.getName()); 
        return authentication.getName(); 
    }
}
