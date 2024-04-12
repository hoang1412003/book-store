package com.tvh.bookstore.service;

import java.util.HashMap;
import org.springframework.security.core.userdetails.UserDetails;



public interface IJWTUtil {
    public String generateToken(UserDetails userDetails);
    public String generateRefreshToken(HashMap<String,Object>claims,UserDetails userDetails);
    public String extractUsername(String token);
    public boolean isTokenValid(String token, UserDetails userDetails);
    public boolean isTokenExpired(String token);
}
