package com.tvh.bookstore.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tvh.bookstore.service.IJWTUtil;
import com.tvh.bookstore.service.impl.OurUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
@Component
public class JWTAuthFIlter extends OncePerRequestFilter {

    // Tiêm IJWTUtil và OurUserDetailsService vào lớp này
    @Autowired
    private IJWTUtil ijwtUtil;
    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // Lấy giá trị của header "Authorization" từ request
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        // Nếu không có header "Authorization" thì cho request đi qua filter tiếp theo
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy giá trị của JWT token từ header "Authorization"
        // Header "Authorization" thường có dạng "Bearer <token>"
        // Nên cần cắt đi 7 ký tự đầu tiên để lấy giá trị token
        jwtToken = authHeader.substring(7);

        // Lấy email của user từ JWT token
        userEmail = ijwtUtil.extractUsername(jwtToken);

        // Nếu có email và chưa có xác thực trong SecurityContext
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Lấy thông tin UserDetails từ email
            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail);

            // Nếu JWT token hợp lệ
            if (ijwtUtil.isTokenValid(jwtToken, userDetails)) {
                // Tạo một đối tượng xác thực mới
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Xây dựng thông tin chi tiết về yêu cầu web
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Tạo một SecurityContext mới và đặt đối tượng xác thực vào đó
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authToken);

                // Đặt SecurityContext mới vào SecurityContextHolder
                SecurityContextHolder.setContext(securityContext);
            }
        }

        // Cho request đi qua filter tiếp theo
        filterChain.doFilter(request, response);
    }
}
