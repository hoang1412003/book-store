package com.tvh.bookstore.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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
        ReqRes reqRes= new ReqRes(); // Khởi tạo đối tượng ReqRes để chứa kết quả trả về
        try {
            // Tạo một đối tượng User mới từ thông tin trong yêu cầu đăng ký
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
            user.setRole(registrationRequest.getRole());
            // Lưu người dùng vào cơ sở dữ liệu và nhận kết quả trả về
            User userResult = iUserRepository.save(user);
            if (userResult != null && userResult.getIdUser()>0){
                reqRes.setUser(userResult); // Set người dùng đã lưu vào kết quả trả về
                reqRes.setMessage("User saved Successfully");
                reqRes.setStatusCode(200); // Thiết lập mã trạng thái thành công
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500); // Nếu có lỗi, đặt mã trạng thái lỗi nội bộ
            reqRes.setError(e.getMessage()); // Đặt thông báo lỗi trong trường hợp có ngoại lệ
        }
        return reqRes; // Trả về kết quả
    }

    // Phương thức đăng nhập
    @Override
    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes reqRes= new ReqRes(); // Khởi tạo đối tượng ReqRes để chứa kết quả trả về
        try {
            // Xác thực thông tin người dùng
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            // Tìm người dùng trong cơ sở dữ liệu dựa trên email và lấy ra nếu tồn tại
            var user1= iUserRepository.findByEmail(signinRequest.getEmail()).orElseThrow();    
            System.out.println("User is: "+user1);
            // Tạo JWT và refreshToken
            var jwt = ijwtUtil.generateToken(user1);
            var refreshToken = ijwtUtil.generateRefreshToken(new HashMap<>(), user1);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshToken);
            reqRes.setExpirationTime("1Hrs");
            reqRes.setMessage("Successfully Signed In");
        } catch (Exception e) {
            reqRes.setStatusCode(500); // Nếu có lỗi, đặt mã trạng thái lỗi nội bộ
            reqRes.setError(e.getMessage()); // Đặt thông báo lỗi trong trường hợp có ngoại lệ
        }
        return reqRes; // Trả về kết quả
    }

    // Phương thức làm mới JWT
    @Override
    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes reqRes = new ReqRes(); // Khởi tạo đối tượng ReqRes để chứa kết quả trả về
        String userEmail= ijwtUtil.extractUsername(refreshTokenReqiest.getToken());
        // Tìm người dùng trong cơ sở dữ liệu dựa trên email và lấy ra nếu tồn tại
        User user = iUserRepository.findByEmail(userEmail).orElseThrow();
        // Kiểm tra tính hợp lệ của token và làm mới token
        if (ijwtUtil.isTokenValid(refreshTokenReqiest.getToken(), user)){
            var jwt = ijwtUtil.generateToken(user);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshTokenReqiest.getToken());
            reqRes.setExpirationTime("1Hr");
            reqRes.setMessage("Successfully Refreshed Token");
        }
       reqRes.setStatusCode(500); // Nếu có lỗi, đặt mã trạng thái lỗi nội bộ
       return reqRes; // Trả về kết quả
    }
    

}
