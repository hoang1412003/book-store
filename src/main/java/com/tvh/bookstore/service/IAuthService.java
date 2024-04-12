package com.tvh.bookstore.service;

import dto.ReqRes;

public interface IAuthService {
    public ReqRes signUp(ReqRes registrationRequest);
    public ReqRes signIn(ReqRes signinRequest);
    public ReqRes refreshToken(ReqRes refreshTokenReqiest);
}
