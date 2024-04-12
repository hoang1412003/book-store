package com.tvh.bookstore.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUsers {

    // API chỉ dành cho người dùng
    @GetMapping("user/alone")
    public ResponseEntity<Object>userAlone(){
        return ResponseEntity.ok("USers alone can access this ApI only");
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
