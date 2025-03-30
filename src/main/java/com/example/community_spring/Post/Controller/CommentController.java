package com.example.community_spring.Post.Controller;

import com.example.community_spring.Post.DTO.request.CreateCommentRequest;
import com.example.community_spring.Post.DTO.request.UpdateCommentRequest;
import com.example.community_spring.Post.DTO.response.CommentResponse;
import com.example.community_spring.Post.Service.CommentService;
import com.example.community_spring.User.DTO.response.ApiResponse;
import com.example.community_spring.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 특정 게시글의 댓글 목록 조회 API
     * GET /api/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> getCommentsByPostId(@PathVariable Long postId) {
        try {
            log.info("댓글 목록 조회 요청: 게시글 ID {}", postId);

            List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("댓글 목록 조회에 성공했습니다.")
                    .data(comments)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("댓글 목록 조회 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("댓글 목록 조회 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 댓글 작성 API
     * POST /api/posts/{postId}/comments
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<?>> createComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 내용 검증
            if (!StringUtils.hasText(request.getContent())) {
                return getBadRequestResponse("댓글 내용은 필수입니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("댓글 작성 요청: 사용자 ID {}, 게시글 ID {}", userId, postId);

            CommentResponse createdComment = commentService.createComment(postId, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.builder()
                            .success(true)
                            .message("댓글이 작성되었습니다.")
                            .data(createdComment)
                            .build());
        } catch (IllegalArgumentException e) {
            log.warn("댓글 작성 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 댓글 수정 API
     * PUT /api/comments/{commentId}
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 내용 검증
            if (!StringUtils.hasText(request.getContent())) {
                return getBadRequestResponse("댓글 내용은 필수입니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("댓글 수정 요청: 사용자 ID {}, 댓글 ID {}", userId, commentId);

            CommentResponse updatedComment = commentService.updateComment(commentId, userId, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("댓글이 수정되었습니다.")
                    .data(updatedComment)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("댓글 수정 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 댓글 삭제 API
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long commentId) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("댓글 삭제 요청: 사용자 ID {}, 댓글 ID {}", userId, commentId);

            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("댓글이 삭제되었습니다.")
                    .data(null)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("댓글 삭제 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
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
        String jwt = authHeader.substring(7);

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