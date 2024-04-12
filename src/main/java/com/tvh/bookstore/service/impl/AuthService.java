package com.tvh.bookstore.service.impl;

import java.util.HashMap;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tvh.bookstore.entity.User;
import com.tvh.bookstore.repository.IUserRepository;
import com.tvh.bookstore.service.IAuthService;
import com.tvh.bookstore.service.IJWTUtil;

import dto.ReqRes;

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

    @Override
    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes reqRes= new ReqRes();
        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(registrationRequest.getPassword());
            user.setRole(registrationRequest.getRole());
            User userResult = iUserRepository.save(user);
            if (userResult != null && userResult.getIdUser()>0){
                reqRes.setUser(userResult);
                reqRes.setMessage("User saved Successfully");
                reqRes.setStatusCode(200);
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }
    @Override
    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes reqRes= new ReqRes();
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), reqRes.getPassword()));
            var user1= iUserRepository.findByEmail(signinRequest.getEmail()).orElseThrow();    
            System.out.println("User is: "+user1);
            var jwt = ijwtUtil.generateToken(user1);
            var refreshToken = ijwtUtil.generateRefreshToken(new HashMap<>(), user1);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshToken);
            reqRes.setExpirationTime("2,4Hrs");
            reqRes.setMessage("Successfully Signed In");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }return reqRes;
    }
    @Override
    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }

    

}
