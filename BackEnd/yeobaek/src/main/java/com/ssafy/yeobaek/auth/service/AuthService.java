package com.ssafy.yeobaek.auth.service;

import com.ssafy.yeobaek.auth.dto.request.LoginRequest;
import com.ssafy.yeobaek.auth.dto.request.SignupRequest;
import com.ssafy.yeobaek.auth.dto.response.EmailCheckData;
import com.ssafy.yeobaek.auth.dto.response.LoginData;
import com.ssafy.yeobaek.auth.dto.response.TokenData;

public interface AuthService {

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return EmailCheckData (available: true면 사용 가능, false면 이미 사용 중)
     */
    EmailCheckData checkEmailDuplicate(String email);

    /**
     * 회원가입
     * @param request 회원가입 요청 정보
     */
    void signup(SignupRequest request);

    /**
     * 로그인
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return LoginData (accessToken, refreshToken, userType)
     */
    LoginData login(LoginRequest request);

    /**
     * 로그아웃
     * - Redis에서 Refresh Token 삭제
     */
    void logout();

    /**
     * Access Token 재발급
     * @param refreshToken Refresh Token
     * @return TokenData (새로운 accessToken, refreshToken)
     */
    TokenData reissue(String refreshToken);
}
