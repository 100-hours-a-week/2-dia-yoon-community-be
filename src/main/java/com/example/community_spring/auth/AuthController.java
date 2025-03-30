package com.example.community_spring.auth;

import com.example.community_spring.User.DTO.request.LoginRequest;
import com.example.community_spring.User.DTO.request.SignupRequest;
import com.example.community_spring.User.DTO.response.ApiResponse;
import com.example.community_spring.User.DTO.response.UserResponse;
import com.example.community_spring.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 api
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody SignupRequest request) {
        // 이메일 검증
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        // 수동 비밀번호 검증
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (request.getPassword().length() < 8 || request.getPassword().length() > 20) {
            throw new IllegalArgumentException("비밀번호는 8자 이상, 20자 이하여야 합니다.");
        }

        // 수동 닉네임 검증
        if (!StringUtils.hasText(request.getNickname())) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (request.getNickname().length() < 2 || request.getNickname().length() > 50) {
            throw new IllegalArgumentException("닉네임은 2자 이상 50자 이하여야 합니다.");
        }

        log.info("회원가입 요청: {}", request.getEmail());
        UserResponse user = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("회원가입이 완료되었습니다.")
                        .data(user)
                        .build());
    }

    /**
     * 로그인 API
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        try {
            // 수동 이메일 검증
            if (!StringUtils.hasText(request.getEmail())) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.builder()
                                .success(false)
                                .message("이메일은 필수입니다.")
                                .data(null)
                                .build()
                );
            }

            // 수동 비밀번호 검증
            if (!StringUtils.hasText(request.getPassword())) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.builder()
                                .success(false)
                                .message("비밀번호는 필수입니다.")
                                .data(null)
                                .build()
                );
            }

            log.info("로그인 요청: {}", request.getEmail());

            Map<String, Object> result = authService.login(request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("로그인에 성공했습니다.")
                    .data(result)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        } catch (Exception e) {
            log.error("로그인 중 서버 오류: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("서버 오류가 발생했습니다.")
                            .data(null)
                            .build());
        }
    }

    /**
     * 토큰 검증 API
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<?>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // 토큰 형식 검증
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.builder()
                                .success(false)
                                .message("유효하지 않은 토큰 형식입니다.")
                                .data(null)
                                .build());
            }

            // 헤더에서 Bearer 제거 후 토큰 문자열 가져오기
            String token = authHeader.substring(7);

            // 토큰 유효성 검증
            boolean isValid = authService.validateToken(token);

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.builder()
                                .success(false)
                                .message("만료되거나 유효하지 않은 토큰입니다.")
                                .data(null)
                                .build());
            }

            // 토큰에서 사용자 ID 추출
            Long userId = authService.getUserIdFromToken(token);

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("유효한 토큰입니다.")
                    .data(Map.of("userId", userId))
                    .build());
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("토큰 검증 중 오류가 발생했습니다.")
                            .data(null)
                            .build());
        }
    }

    /**
     * 이메일 중복 확인 API
     * GET /api/auth/check-email?email={email}
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<?>> checkEmail(@RequestParam String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        log.info("이메일 중복 확인 요청: {}", email);
        boolean isDuplicate = authService.isEmailDuplicate(email);

        Map<String, Boolean> result = Map.of("isDuplicate", isDuplicate);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("이메일 중복 확인이 완료되었습니다.")
                .data(result)
                .build());
    }

    /**
     * 닉네임 중복 확인 API
     * GET /api/auth/check-nickname?nickname={nickname}
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<?>> checkNickname(@RequestParam String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }

        log.info("닉네임 중복 확인 요청: {}", nickname);
        boolean isDuplicate = authService.isNicknameDuplicate(nickname);

        Map<String, Boolean> result = Map.of("isDuplicate", isDuplicate);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("닉네임 중복 확인이 완료되었습니다.")
                .data(result)
                .build());
    }
}