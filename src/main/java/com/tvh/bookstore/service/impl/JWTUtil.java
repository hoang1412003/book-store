package com.tvh.bookstore.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.tvh.bookstore.service.IJWTUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil implements IJWTUtil {

    private SecretKey key;// Khóa bí mật được sử dụng để ký và xác thực JWT
    private static final long EXPIRATION_TIME= 3600;

    // Constructor, khởi tạo khóa bí mật ngẫu nhiên
    public JWTUtil(){
        String secretString = generateRandomSecrect(); // Tạo chuỗi bí mật ngẫu nhiên
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8)); // Giải mã chuỗi ngẫu nhiên thành mảng byte
        this.key=new SecretKeySpec(keyBytes, "HmacSHA256"); // Tạo đối tượng SecretKey từ mảng byte, sử dụng thuật toán HmacSHA256
    }

    // Phương thức tạo chuỗi bí mật ngẫu nhiên
    private String generateRandomSecrect(){
        int length=32; // Độ dài chuỗi bí mật
        byte[] randomBytes=new byte[length]; // Mảng byte để lưu chuỗi bí mật ngẫu nhiên
        new SecureRandom().nextBytes(randomBytes); // Sinh chuỗi bí mật ngẫu nhiên
        return Base64.getEncoder().encodeToString(randomBytes); // Mã hóa mảng byte thành chuỗi Base64
    }

    // Phương thức tạo JWT từ UserDetails
    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
        .subject(userDetails.getUsername()) // Thiết lập subject là username của UserDetails
        .issuedAt(new Date(System.currentTimeMillis())) // Thiết lập thời gian phát hành là thời điểm hiện tại
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Thiết lập thời gian hết hạn
        .signWith(key) // Ký JWT bằng khóa bí mật
        .compact(); // Tạo JWT
    }   

    // Phương thức tạo refreshToken từ UserDetails và các claims
    @Override
    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
        .claims(claims) // Thiết lập các claims cho refreshToken
        .subject(userDetails.getUsername()) // Thiết lập subject là username của UserDetails
        .issuedAt(new Date(System.currentTimeMillis())) // Thiết lập thời gian phát hành là thời điểm hiện tại
        .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME)) // Thiết lập thời gian hết hạn
        .signWith(key) // Ký refreshToken bằng khóa bí mật
        .compact(); // Tạo refreshToken
    }

    // Phương thức trích xuất username từ JWT
    @Override
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject); // Sử dụng phương thức extractClaims với function là getSubject để lấy username
    }

    // Phương thức trích xuất các claims từ JWT và áp dụng function được cung cấp
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()); // Parse và xác thực JWT, sau đó áp dụng function để trích xuất claims
    }
    
    // Phương thức kiểm tra tính hợp lệ của JWT
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token); // Trích xuất username từ JWT
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Kiểm tra username và xem token có hết hạn không
    }

    // Phương thức kiểm tra xem JWT có hết hạn không
    @Override
    public boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date()); // Trích xuất thời gian hết hạn từ JWT và so sánh với thời gian hiện tại
    }
}
