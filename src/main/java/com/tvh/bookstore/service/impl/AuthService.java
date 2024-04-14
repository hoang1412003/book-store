package com.tvh.bookstore.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tvh.bookstore.dto.ReqRes;
import com.tvh.bookstore.entity.User;
import com.tvh.bookstore.repository.IUserRepository;
import com.tvh.bookstore.service.IAuthService;
import com.tvh.bookstore.service.IJWTUtil;


@Service
public class AuthService implements IAuthService {

    @Autowired
    private IUserRepository iUserRepository;
    @Autowired
    private IJWTUtil ijwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    // Phương thức đăng ký người dùng mới
    @Override
    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes reqRes = new ReqRes();
    
        try {
            // Kiểm tra xem username đã tồn tại hay chưa
            if (iUserRepository.existsByUsername(registrationRequest.getUserName())) {
                reqRes.setStatusCode(400); // Mã lỗi BadRequest khi username đã tồn tại
                reqRes.setError("Username already exists");
                return reqRes;
            }
    
            // Kiểm tra xem email đã tồn tại hay chưa
            if (iUserRepository.existsByEmail(registrationRequest.getEmail())) {
                reqRes.setStatusCode(400); // Mã lỗi BadRequest khi email đã tồn tại
                reqRes.setError("Email already exists");
                return reqRes;
            }
    
            // Tạo một đối tượng User mới từ thông tin trong yêu cầu đăng ký
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setUserName(registrationRequest.getUserName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
            user.setRole(registrationRequest.getRole());
    
            // Lưu người dùng vào cơ sở dữ liệu và nhận kết quả trả về
            User userResult = iUserRepository.save(user);
    
            if (userResult != null && userResult.getIdUser() > 0) {
                reqRes.setStatusCode(200); // Mã thành công
                reqRes.setMessage("User saved Successfully");
                reqRes.setUser(userResult); // Set người dùng đã lưu vào kết quả trả về
            } else {
                reqRes.setStatusCode(500); // Mã lỗi nếu không thể lưu người dùng
                reqRes.setError("Failed to save user");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500); // Mã lỗi nếu có ngoại lệ
            reqRes.setError("Internal Server Error: " + e.getMessage());
        }
    
        return reqRes;
    }
    

    // Phương thức đăng nhập
    @Override
    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes reqRes = new ReqRes();
    
        try {
            // Xác thực thông tin người dùng
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
    
            // Tìm người dùng trong cơ sở dữ liệu dựa trên email và lấy ra nếu tồn tại
            var user = iUserRepository.findByEmail(signinRequest.getEmail())
                                       .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
    
            // Tạo JWT và refreshToken
            var jwt = ijwtUtil.generateToken(user);
            var refreshToken = ijwtUtil.generateRefreshToken(new HashMap<>(), user);
    
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshToken);
            reqRes.setExpirationTime("1Hrs");
            reqRes.setMessage("Successfully Signed In");
        } catch (BadCredentialsException e) {
            reqRes.setStatusCode(401); // Unauthorized
            reqRes.setError("Invalid email or password");
        } catch (Exception e) {
            reqRes.setStatusCode(500); // Internal Server Error
            reqRes.setError(e.getMessage()); // Đặt thông báo lỗi trong trường hợp có ngoại lệ
        }
    
        return reqRes;
    }
    
    

    // Phương thức làm mới JWT
    @Override
    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes reqRes = new ReqRes();
    
        try {
            String userEmail = ijwtUtil.extractUsername(refreshTokenRequest.getToken());
            // Tìm người dùng trong cơ sở dữ liệu dựa trên email và lấy ra nếu tồn tại
            User user = iUserRepository.findByEmail(userEmail).orElseThrow();
    
            // Kiểm tra tính hợp lệ của token và làm mới token
            if (ijwtUtil.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = ijwtUtil.generateToken(user); // Tạo token mới
                reqRes.setStatusCode(200);
                reqRes.setToken(jwt);
                reqRes.setRefreshToken(refreshTokenRequest.getToken());
                reqRes.setExpirationTime("1Hr");
                reqRes.setMessage("Successfully Refreshed Token");
            } else {
                reqRes.setStatusCode(401); // Unauthorized nếu token không hợp lệ
                reqRes.setError("Invalid or expired token");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500); // Internal Server Error nếu có lỗi
            reqRes.setError(e.getMessage());
        }
    
        return reqRes;
    }
    
    

}
