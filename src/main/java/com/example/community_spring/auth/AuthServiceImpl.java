package com.example.community_spring.auth;

import com.example.community_spring.User.DTO.request.LoginRequest;
import com.example.community_spring.User.DTO.request.SignupRequest;
import com.example.community_spring.User.DTO.response.UserResponse;
import com.example.community_spring.User.Entity.User;
import com.example.community_spring.User.Repository.UserRepository;
import com.example.community_spring.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성 (암호화된 비밀번호 사용)
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .profileImage(request.getProfileImage())
                .build();

        // JPA 방식으로 저장 및 반환된 엔티티 사용
        User savedUser = userRepository.save(user);

        // UserResponse 객체 생성 및 반환
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginRequest request) {
        try {
            // 이메일로 사용자 찾기
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

            // 비밀번호 검증 (matches 메소드 사용)
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(user.getUserId());

            // 응답 데이터 생성
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", user.getUserId());
            userData.put("nickname", user.getNickname());
            userData.put("email", user.getEmail());
            userData.put("profile_image", user.getProfileImage());

            result.put("token", token);
            result.put("user", userData);

            return result;
        } catch (Exception e) {
            System.err.println("로그인 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
        // JWT 토큰 생성으로 변경
        return jwtTokenProvider.generateToken(userId);
    }

    @Override
    public boolean validateToken(String token) {
        // JWT 토큰 검증으로 변경
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        // JWT에서 사용자 ID 추출
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}