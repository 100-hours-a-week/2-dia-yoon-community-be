package com.example.community_spring.Post.Service;

import com.example.community_spring.Post.DTO.request.CreatePostRequest;
import com.example.community_spring.Post.DTO.request.UpdatePostRequest;
import com.example.community_spring.Post.DTO.response.PostListResponse;
import com.example.community_spring.Post.DTO.response.PostResponse;
import com.example.community_spring.Post.Entity.Post;
import com.example.community_spring.Post.Repository.LikesRepository;
import com.example.community_spring.Post.Repository.CommentRepository;
import com.example.community_spring.Post.Repository.PostRepository;
import com.example.community_spring.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikesRepository LikesRepository;
    private final CommentRepository CommentRepository;

    private static final int PAGE_SIZE = 10; // 페이지당 게시글 수

    /**
     * 게시글 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public PostListResponse getPosts(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        List<Post> posts = postRepository.findAll(PAGE_SIZE, offset);

        // Entity -> DTO 변환
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        // 전체 게시글 수와 페이지 정보 계산
        int totalPosts = postRepository.countAll();
        int totalPages = (int) Math.ceil((double) totalPosts / PAGE_SIZE);

        return PostListResponse.builder()
                .posts(postResponses)
                .currentPage(page)
                .totalPages(totalPages)
                .totalPosts(totalPosts)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }

    /**
     * 특정 사용자의 게시글 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public PostListResponse getPostsByUserId(Long userId, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        List<Post> posts = postRepository.findByUserId(userId, PAGE_SIZE, offset);

        // Entity -> DTO 변환
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        // 해당 사용자의 전체 게시글 수와 페이지 정보 계산
        int totalPosts = postRepository.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalPosts / PAGE_SIZE);

        return PostListResponse.builder()
                .posts(postResponses)
                .currentPage(page)
                .totalPages(totalPages)
                .totalPosts(totalPosts)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 조회수 증가
        postRepository.incrementViews(postId);

        // 최신 데이터로 다시 조회
        post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return PostResponse.fromEntity(post);
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        // 사용자가 존재하는지 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 게시글 엔티티 생성
        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .postImage(request.getPostImage())
                .build();

        // 게시글 저장
        Long postId = postRepository.save(post);

        // 저장된 게시글 조회
        return getPost(postId);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse updatePost(Long userId, Long postId, UpdatePostRequest request) {
        // 게시글이 존재하는지 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 게시글 작성자와 요청자가 일치하는지 확인
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        // 게시글 업데이트
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostImage(request.getPostImage());

        postRepository.update(post);

        // 업데이트된 게시글 조회
        return getPost(postId);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        // 게시글이 존재하는지 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 게시글 작성자와 요청자가 일치하는지 확인
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        // 관련 좋아요 데이터 먼저 삭제
        LikesRepository.deleteAllByPostId(postId);

        // 관련 댓글 데이터 삭제
        CommentRepository.deleteAllByPostId(postId);

        // 마지막으로 게시글 삭제
        postRepository.delete(postId);
    }
}