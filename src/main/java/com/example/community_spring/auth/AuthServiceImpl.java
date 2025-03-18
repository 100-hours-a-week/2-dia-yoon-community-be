package com.example.community_spring.auth;

import com.example.community_spring.User.DTO.request.LoginRequest;
import com.example.community_spring.User.DTO.request.SignupRequest;
import com.example.community_spring.User.DTO.response.UserResponse;
import com.example.community_spring.User.Entity.User;
import com.example.community_spring.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse register(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 해싱 (간단한 MD5 해싱)
        String hashedPassword = hashPassword(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .nickname(request.getNickname())
                .profileImage(request.getProfileImage())
                .build();

        // 저장 후 ID 받기
        Long userId = userRepository.save(user);

        // 저장된 사용자 조회
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 저장 후 조회 실패"));

        // UserResponse 객체 생성 및 반환
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginRequest request) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 검증
        String hashedPassword = hashPassword(request.getPassword());
        if (!hashedPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 생성
        String token = generateToken(user.getUserId());

        // 응답 데이터 생성
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", UserResponse.fromEntity(user));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public String generateToken(Long userId) {
        // 간단한 토큰 생성 로직
        return "token-" + UUID.randomUUID().toString() + "-" + userId;
    }

    @Override
    public boolean validateToken(String token) {
        // 토큰 유효성 검사 로직
        return token != null && token.startsWith("token-");
    }

    @Override
    public Long getUserIdFromToken(String token) {
        // 토큰에서 사용자 ID 추출
        int lastDashIndex = token.lastIndexOf('-');
        if (lastDashIndex == -1) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
        }

        try {
            return Long.parseLong(token.substring(lastDashIndex + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("토큰에서 사용자 ID를 추출할 수 없습니다.");
        }
    }

    // 간단한 비밀번호 해싱 (실제 환경에서는 더 안전한 방식 권장)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 매우 기본적인 해시
            return password + "hashed";
        }
    }
}