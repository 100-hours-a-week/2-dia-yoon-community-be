package com.example.community_spring.Post.Controller;

import com.example.community_spring.Post.Service.LikesService;
import com.example.community_spring.User.DTO.response.ApiResponse;
import com.example.community_spring.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 좋아요 토글 API
     * POST /api/posts/{postId}/likes
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<?>> toggleLike(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("좋아요 토글 요청: 사용자 ID {}, 게시글 ID {}", userId, postId);

            // 좋아요 토글 처리
            boolean isLiked = likesService.toggleLike(postId, userId);

            // 응답 데이터 생성
            Map<String, Object> data = new HashMap<>();
            data.put("liked", isLiked);
            data.put("likeCount", likesService.getLikeCount(postId));

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message(isLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.")
                    .data(data)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("좋아요 토글 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 좋아요 상태 조회 API
     * GET /api/posts/{postId}/likes/status
     */
    @GetMapping("/{postId}/likes/status")
    public ResponseEntity<ApiResponse<?>> getLikeStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("좋아요 상태 조회 요청: 사용자 ID {}, 게시글 ID {}", userId, postId);

            // 좋아요 상태 조회
            boolean isLiked = likesService.getLikeStatus(postId, userId);
            int likeCount = likesService.getLikeCount(postId);

            // 응답 데이터 생성
            Map<String, Object> data = new HashMap<>();
            data.put("liked", isLiked);
            data.put("likeCount", likeCount);

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("좋아요 상태 조회에 성공했습니다.")
                    .data(data)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("좋아요 상태 조회 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("좋아요 상태 조회 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 토큰에서 사용자 ID 추출 (JWT 방식으로 변경)
     */
    private Long extractUserIdFromToken(String authHeader) {
        // 토큰 형식 검증
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 헤더에서 Bearer 제거 후 토큰 문자열 가져오기
        String jwt = authHeader.substring(7);

        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(jwt)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // JWT 토큰에서 사용자 ID 추출
        return jwtTokenProvider.getUserIdFromToken(jwt);
    }

    /**
     * Bad Request 응답 생성
     */
    private ResponseEntity<ApiResponse<?>> getBadRequestResponse(String message) {
        return ResponseEntity.badRequest()
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
        return ResponseEntity.status(401)
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
        return ResponseEntity.status(500)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("서버 오류가 발생했습니다.")
                        .data(null)
                        .build());
    }
}