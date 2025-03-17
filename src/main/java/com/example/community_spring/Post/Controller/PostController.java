package com.example.community_spring.Post.Controller;

import com.example.community_spring.Post.DTO.request.CreatePostRequest;
import com.example.community_spring.Post.DTO.request.UpdatePostRequest;
import com.example.community_spring.Post.DTO.response.PostListResponse;
import com.example.community_spring.Post.DTO.response.PostResponse;
import com.example.community_spring.Post.Service.PostService;
import com.example.community_spring.User.DTO.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 목록 조회 API
     * GET /api/posts?page={page}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPosts(
            @RequestParam(defaultValue = "1") int page) {
        try {
            log.info("게시글 목록 조회 요청: 페이지 {}", page);

            if (page < 1) {
                return getBadRequestResponse("페이지 번호는 1 이상이어야 합니다.");
            }

            PostListResponse response = postService.getPosts(page);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("게시글 목록 조회에 성공했습니다.")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 게시글 상세 조회 API
     * GET /api/posts/{post_id}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getPost(@PathVariable Long postId) {
        try {
            log.info("게시글 상세 조회 요청: 게시글 ID {}", postId);

            PostResponse post = postService.getPost(postId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("게시글 조회에 성공했습니다.")
                    .data(post)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("게시글 조회 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 게시글 작성 API
     * POST /api/posts
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreatePostRequest request) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("게시글 작성 요청: 사용자 ID {}", userId);

            // 제목 검증
            if (!StringUtils.hasText(request.getTitle())) {
                return getBadRequestResponse("제목은 필수입니다.");
            }

            // 내용 검증
            if (!StringUtils.hasText(request.getContent())) {
                return getBadRequestResponse("내용은 필수입니다.");
            }

            PostResponse createdPost = postService.createPost(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.builder()
                            .success(true)
                            .message("게시글이 작성되었습니다.")
                            .data(createdPost)
                            .build());
        } catch (IllegalArgumentException e) {
            log.warn("게시글 작성 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 게시글 수정 API
     * PUT /api/posts/{post_id}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> updatePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("게시글 수정 요청: 사용자 ID {}, 게시글 ID {}", userId, postId);

            // 제목 검증
            if (!StringUtils.hasText(request.getTitle())) {
                return getBadRequestResponse("제목은 필수입니다.");
            }

            // 내용 검증
            if (!StringUtils.hasText(request.getContent())) {
                return getBadRequestResponse("내용은 필수입니다.");
            }

            PostResponse updatedPost = postService.updatePost(userId, postId, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("게시글이 수정되었습니다.")
                    .data(updatedPost)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("게시글 수정 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 게시글 삭제 API
     * DELETE /api/posts/{post_id}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        try {
            // 토큰 검증
            if (!StringUtils.hasText(authHeader)) {
                return getUnauthorizedResponse("인증 토큰이 필요합니다.");
            }

            // 토큰에서 사용자 ID 추출
            Long userId = extractUserIdFromToken(authHeader);
            log.info("게시글 삭제 요청: 사용자 ID {}, 게시글 ID {}", userId, postId);

            postService.deletePost(userId, postId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("게시글이 삭제되었습니다.")
                    .data(null)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("게시글 삭제 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            return getServerErrorResponse();
        }
    }

    /**
     * 특정 사용자의 게시글 목록 조회 API
     * GET /api/posts/user/{userId}?page={page}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page) {
        try {
            log.info("사용자 게시글 목록 조회 요청: 사용자 ID {}, 페이지 {}", userId, page);

            if (page < 1) {
                return getBadRequestResponse("페이지 번호는 1 이상이어야 합니다.");
            }

            PostListResponse response = postService.getPostsByUserId(userId, page);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("사용자 게시글 목록 조회에 성공했습니다.")
                    .data(response)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("사용자 게시글 목록 조회 실패: {}", e.getMessage());
            return getBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            log.error("사용자 게시글 목록 조회 중 오류 발생", e);
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