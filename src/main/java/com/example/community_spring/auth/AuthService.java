package com.example.community_spring.auth;

import com.example.community_spring.User.DTO.request.LoginRequest;
import com.example.community_spring.User.DTO.request.SignupRequest;
import com.example.community_spring.User.DTO.response.UserResponse;

import java.util.Map;

public interface AuthService {
    /**
     * 회원가입 처리
     */
    UserResponse register(SignupRequest request);

    /**
     * 로그인 처리
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 이메일 중복 체크
     */
    boolean isEmailDuplicate(String email);

    /**
     * 닉네임 중복 체크
     */
    boolean isNicknameDuplicate(String nickname);

    /**
     * 토큰 발급
     */
    String generateToken(Long userId);

    /**
     * 토큰 검증
     */
    boolean validateToken(String token);

    /**
     * 토큰에서 사용자 ID 추출
     */
    Long getUserIdFromToken(String token);
}