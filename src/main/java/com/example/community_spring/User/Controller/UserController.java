package com.example.community_spring.User.Controller;

import com.example.community_spring.User.DTO.request.*;
import com.example.community_spring.User.DTO.response.*;
import com.example.community_spring.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * POST /api/users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody SignupRequest request) {
        try {
            // 수동 이메일 검증
            if (!StringUtils.hasText(request.getEmail())) {
                return getBadRequestResponse("이메일은 필수입니다.");
            }

            // 수동 비밀번호 검증
            if (!StringUtils.hasText(request.getPassword())) {
                return getBadRequestResponse("비밀번호는 필수입니다.");
            }
            if (request.getPassword().length() < 6) {
                return getBadRequestResponse("비밀번호는 최소 6자 이상이어야 합니다.");
            }

            // 수동 닉네임 검증
            if (!StringUtils.hasText(request.getNickname())) {
                return getBadRequestResponse("닉네임은 필수입니다.");
            }
            if (request.getNickname().length() < 2 || request.getNickname().length() > 50) {
                return getBadRequestResponse("닉네임은 2자 이상 50자 이하여야 합니다.");
            }

            log.info("회원가입 요청: {}", request.getEmail());
            UserResponse user = userService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UserResponse>builder()
                            .success(true)
                            .message("회원가입이 완료되었습니다.")
                            .data(user)
                            .build());
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 로그인 API
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        try {
            // 수동 이메일 검증
            if (!StringUtils.hasText(request.getEmail())) {
                return getBadRequestResponse("이메일은 필수입니다.");
            }

            // 수동 비밀번호 검증
            if (!StringUtils.hasText(request.getPassword())) {
                return getBadRequestResponse("비밀번호는 필수입니다.");
            }

            log.info("로그인 요청: {}", request.getEmail());
            Map<String, Object> result = userService.login(request);
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
            log.error("로그인 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 현재 사용자 프로필 조회 API
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("프로필 조회 요청: 사용자 ID {}", userId);

            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("프로필 조회에 성공했습니다.")
                    .data(user)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("프로필 조회 실패: {}", e.getMessage());
            return getUnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            log.error("프로필 조회 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 프로필 업데이트 API
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileRequest request) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 닉네임 검증
            if (StringUtils.hasText(request.getNickname()) &&
                    (request.getNickname().length() < 2 || request.getNickname().length() > 50)) {
                return getBadRequestResponse("닉네임은 2자 이상 50자 이하여야 합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("프로필 업데이트 요청: 사용자 ID {}", userId);

            UserResponse updatedUser = userService.updateProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("프로필이 업데이트되었습니다.")
                    .data(updatedUser)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("프로필 업데이트 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("프로필 업데이트 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 비밀번호 변경 API
     * PUT /api/users/password
     */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdatePasswordRequest request) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 비밀번호 검증
            if (!StringUtils.hasText(request.getPassword())) {
                return getBadRequestResponse("비밀번호는 필수입니다.");
            }
            if (request.getPassword().length() < 6) {
                return getBadRequestResponse("비밀번호는 최소 6자 이상이어야 합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("비밀번호 변경 요청: 사용자 ID {}", userId);

            userService.updatePassword(userId, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("비밀번호가 변경되었습니다.")
                    .data(null)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 변경 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 회원 탈퇴 API
     * DELETE /api/users
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("회원 탈퇴 요청: 사용자 ID {}", userId);

            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("회원 탈퇴가 완료되었습니다.")
                    .data(null)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("회원 탈퇴 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("회원 탈퇴 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 로그아웃 API
     * POST /api/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        try {
            log.info("로그아웃 요청");
            userService.logout();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("로그아웃 되었습니다.")
                    .data(null)
                    .build());
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    private Long extractUserIdFromToken(String authHeader) {
        // 토큰 형식 검증
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 헤더에서 Bearer 제거 후 토큰 문자열 가져오기
        String tokenString = authHeader.substring(7);

        try {
            // 토큰 형식: "token-uuid-userId" 또는 "token-uuid1-uuid2-uuid3-uuid4-userId"
            // 마지막 '-' 이후의 문자열을 userId로 파싱
            int lastDashIndex = tokenString.lastIndexOf('-');
            if (lastDashIndex == -1) {
                throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
            }

            String userIdStr = tokenString.substring(lastDashIndex + 1);
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("토큰에서 사용자 ID를 추출할 수 없습니다.");
        }
    }

    /**
     * Bad Request 응답 생성
     */
    private ResponseEntity<ApiResponse<?>> getBadRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build());
    }

    /**
     * Unauthorized 응답 생성
     */
    private ResponseEntity<ApiResponse<?>> getUnauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build());
    }

    /**
     * Server Error 응답 생성
     */
    private ResponseEntity<ApiResponse<?>> getServerErrorResponse() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("서버 오류가 발생했습니다.")
                        .data(null)
                        .build());
    }
}