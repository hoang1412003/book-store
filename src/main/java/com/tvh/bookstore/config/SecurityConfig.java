package com.tvh.bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tvh.bookstore.service.impl.OurUserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Tiêm Inject OurUserDetailsService và JWTAuthFIlter
    @Autowired
    private OurUserDetailsService ourUserDetailsService;
    @Autowired
    private JWTAuthFIlter jwtAuthFIlter;

    // Phương thức tạo ra SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Disable CSRF (Cross-Site Request Forgery) protection
        httpSecurity.csrf(AbstractHttpConfigurer::disable)//Vì đang tạo một API RESTful, nên CSRF protection có thể bị vô hiệu hóa
                // Cho phép cross-origin requests
                .cors(Customizer.withDefaults())
                // Cấu hình quyền truy cập cho các URL
                .authorizeHttpRequests(request -> request.requestMatchers("/auth/**", "/public/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/user/**").hasAnyAuthority("USER")
                        .requestMatchers("/adminuser/**").hasAnyAuthority("USER", "ADMIN")
                        .anyRequest().authenticated())
                // Cấu hình chính sách quản lý phiên
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Cấu hình AuthenticationProvider
                .authenticationProvider(authenticationProvider())
                // Thêm JWTAuthFIlter vào filter chain trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFIlter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    // Phương thức tạo ra AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // Đặt UserDetailsService cho AuthenticationProvider
        daoAuthenticationProvider.setUserDetailsService(ourUserDetailsService);
        // Đặt PasswordEncoder cho AuthenticationProvider
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    // Phương thức tạo ra PasswordEncoder (BCryptPasswordEncoder)
    @Bean
    public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
    }

    // Phương thức tạo ra AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}