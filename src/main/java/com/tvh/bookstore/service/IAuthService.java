package com.tvh.bookstore.service;

import com.tvh.bookstore.dto.ReqRes;

public interface IAuthService {
    public ReqRes signUp(ReqRes registrationRequest);
    public ReqRes signIn(ReqRes signinRequest);
    public ReqRes refreshToken(ReqRes refreshTokenReqiest);
}
