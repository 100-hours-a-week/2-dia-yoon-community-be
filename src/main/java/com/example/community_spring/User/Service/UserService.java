package com.example.community_spring.User.Service;

import com.example.community_spring.User.DTO.request.UpdatePasswordRequest;
import com.example.community_spring.User.DTO.request.UpdateProfileRequest;
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
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

//    /**
//     * 회원가입
//     */
//    @Transactional
//    public UserResponse signup(SignupRequest request) {
//        // 이메일 중복 체크
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
//        }
//
//        // 닉네임 중복 체크
//        if (userRepository.existsByNickname(request.getNickname())) {
//            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
//        }
//
//        // 비밀번호 암호화
//        String encodedPassword = passwordEncoder.encode(request.getPassword());
//
//        // 사용자 생성
//        User user = User.builder()
//                .nickname(request.getNickname())
//                .email(request.getEmail())
//                .password(encodedPassword)
//                .profileImage(request.getProfileImage())
//                .build();
//
//        // JPA를 사용하여 사용자 저장
//        User savedUser = userRepository.save(user);
//
//        // 응답 객체 생성
//        return UserResponse.fromEntity(savedUser);
//    }
//
//    /**
//     * 로그인
//     */
//    @Transactional(readOnly = true)
//    public Map<String, Object> login(LoginRequest request) {
//        // 이메일로 사용자 찾기
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
//
//        // 비밀번호 확인 (BCrypt 매치 사용)
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//
//        // JWT 토큰 생성
//        String token = generateToken(user.getUserId());
//
//        // 응답 데이터 생성
//        Map<String, Object> result = new HashMap<>();
//        Map<String, Object> userData = new HashMap<>();
//        userData.put("user_id", user.getUserId());
//        userData.put("nickname", user.getNickname());
//        userData.put("email", user.getEmail());
//        userData.put("profile_image", user.getProfileImage());
//
//        result.put("token", token);
//        result.put("user", userData);
//
//        return result;
//    }

    /**
     * JWT 토큰 생성 메서드
     */
    private String generateToken(Long userId) {
        return jwtTokenProvider.generateToken(userId);
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

        // 중요: 프로필 이미지 처리 로직
        String profileImage = request.getProfileImage();

        // 프로필 이미지가 null이거나 비어있으면 기존 이미지 유지
        if (profileImage == null || profileImage.isEmpty()) {
            profileImage = user.getProfileImage();
        }

        System.out.println("프로필 업데이트 - 사용자 ID: " + userId);
        System.out.println("변경 전 프로필 이미지: " + user.getProfileImage());
        System.out.println("요청된 프로필 이미지: " + request.getProfileImage());
        System.out.println("최종 적용 프로필 이미지: " + profileImage);

        // 프로필 업데이트 - 닉네임과 프로필 이미지 모두 전달
        userRepository.updateProfile(userId, request.getNickname(), profileImage);

        // 업데이트된 사용자 정보 조회
        user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        System.out.println("업데이트 후 프로필 이미지: " + user.getProfileImage());

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

        // 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userRepository.updatePassword(userId, encodedPassword);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        // 사용자 존재 여부 확인 및 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 삭제 - 엔티티 객체를 전달
        userRepository.delete(user);
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