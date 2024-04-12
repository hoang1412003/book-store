package com.tvh.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        // Tạo một đối tượng WebMvcConfigurer để cấu hình CORS
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Thêm cấu hình CORS vào registry
                registry.addMapping("/**"); 
                // Thiết lập CORS cho tất cả các đường dẫn trong ứng dụng
            }
        };
    }
}