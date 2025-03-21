package com.example.community_spring.User.Service;

import com.example.community_spring.User.DTO.request.LoginRequest;
import com.example.community_spring.User.DTO.request.SignupRequest;
import com.example.community_spring.User.DTO.request.UpdatePasswordRequest;
import com.example.community_spring.User.DTO.request.UpdateProfileRequest;
import com.example.community_spring.User.DTO.response.UserResponse;
import com.example.community_spring.User.Entity.User;
import com.example.community_spring.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 매번 새로운 인스턴스 생성 (기존 의존성 주입 우회)
    private BCryptPasswordEncoder getSimpleEncoder() {
        return new BCryptPasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 상세 로깅 추가
        System.out.println("===== 회원가입 상세 로깅 =====");
        System.out.println("1. SignupRequest에서 받은 원본 비밀번호: " + request.getPassword());
        System.out.println("1. 비밀번호 길이: " + request.getPassword().length());

        String rawPassword = request.getPassword();
        System.out.println("2. 로컬 변수에 복사 후 비밀번호: " + rawPassword);

        // 사용자 생성 직전 로깅
        System.out.println("3. User 객체 생성 직전 비밀번호: " + rawPassword);

        // 사용자 생성
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(rawPassword)
                .profileImage(request.getProfileImage())
                .build();

        System.out.println("4. User 객체 생성 후 비밀번호: " + user.getPassword());

        // User 객체 필드 검사
        System.out.println("User 객체 toString: " + user);
        System.out.println("User 객체 필드 - email: " + user.getEmail());
        System.out.println("User 객체 필드 - nickname: " + user.getNickname());
        System.out.println("User 객체 필드 - password: " + user.getPassword());

        // 저장 직전 로깅
        System.out.println("5. 저장 직전 비밀번호: " + user.getPassword());
        System.out.println("===========================");

        // 저장하고 ID 받기
        Long userId = userRepository.save(user);
        user.setUserId(userId);

        // 응답 객체 생성
        return UserResponse.fromEntity(user);
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginRequest request) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        System.out.println("로그인 시도 - 입력된 비밀번호: " + request.getPassword());
        System.out.println("로그인 시도 - DB에 저장된 비밀번호: " + user.getPassword());

        // 비밀번호 확인 (직접 비교)
        if (!request.getPassword().equals(user.getPassword())) {
            System.out.println("비밀번호 불일치 오류 발생");
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 간단한 토큰 생성
        String token = generateToken(user.getUserId());

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
    }

    /**
     * 간단한 토큰 생성 메서드
     */
    private String generateToken(Long userId) {
        return "token-" + UUID.randomUUID().toString() + "-" + userId;
    }

    /**
     * 프로필 업데이트
     */
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임 중복 확인 (변경하려는 경우에만)
        if (!user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 프로필 업데이트
        userRepository.updateProfile(userId, request.getNickname(), request.getProfileImage());

        // 업데이트된 사용자 정보 조회
        user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 응답 객체 생성
        return UserResponse.fromEntity(user);
    }

    /**
     * 비밀번호 업데이트
     */
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        System.out.println("비밀번호 업데이트 - 새 비밀번호: " + request.getPassword());

        // 비밀번호 업데이트 (암호화 없이 원본 저장)
        userRepository.updatePassword(userId, request.getPassword());

        // 업데이트 후 확인
        User updatedUser = userRepository.findById(userId).orElse(null);
        if (updatedUser != null) {
            System.out.println("업데이트 후 DB에 저장된 비밀번호: " + updatedUser.getPassword());
        }
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteUser(Long userId) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 삭제
        userRepository.delete(userId);
    }

    /**
     * 로그아웃
     * 실제 구현에서는 클라이언트에서 토큰 삭제
     */
    public void logout() {
        // 클라이언트에서 토큰 삭제 처리
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponse.fromEntity(user);
    }
}