package com.example.community_spring.Post.Service;

import com.example.community_spring.Post.DTO.request.CreatePostRequest;
import com.example.community_spring.Post.DTO.request.UpdatePostRequest;
import com.example.community_spring.Post.DTO.response.PostListResponse;
import com.example.community_spring.Post.DTO.response.PostResponse;
import com.example.community_spring.Post.Entity.Post;
import com.example.community_spring.Post.Repository.CommentRepository;
import com.example.community_spring.Post.Repository.LikesRepository;
import com.example.community_spring.Post.Repository.PostRepository;
import com.example.community_spring.User.Entity.User;
import com.example.community_spring.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

    private static final int PAGE_SIZE = 10; // 페이지당 게시글 수

    /**
     * 게시글 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public PostListResponse getPosts(int page) {
        int pageIndex = page - 1; // 페이지 인덱스는 0부터 시작
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());

        // JPA 페이징 적용
        Page<Post> postsPage = postRepository.findAll(pageable);
        List<Post> posts = postsPage.getContent();

        // Entity -> DTO 변환
        List<PostResponse> postResponses = posts.stream()
                .map(this::enrichPostWithUserInfo) // 사용자 정보 보강
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .posts(postResponses)
                .currentPage(page)
                .totalPages(postsPage.getTotalPages())
                .totalPosts((int) postsPage.getTotalElements())
                .hasNext(postsPage.hasNext())
                .hasPrevious(postsPage.hasPrevious())
                .build();
    }

    /**
     * 특정 사용자의 게시글 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public PostListResponse getPostsByUserId(Long userId, int page) {
        int pageIndex = page - 1;
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by("createdAt").descending());

        // JPA 페이징 적용
        Page<Post> postsPage = postRepository.findByUserId(userId, pageable);
        List<Post> posts = postsPage.getContent();

        // Entity -> DTO 변환
        List<PostResponse> postResponses = posts.stream()
                .map(this::enrichPostWithUserInfo)
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .posts(postResponses)
                .currentPage(page)
                .totalPages(postsPage.getTotalPages())
                .totalPosts((int) postsPage.getTotalElements())
                .hasNext(postsPage.hasNext())
                .hasPrevious(postsPage.hasPrevious())
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

        // 사용자 정보 보강 - 명시적으로 로그 출력
        post = enrichPostWithUserInfo(post);
        System.out.println("게시글 조회 결과: postId=" + post.getPostId() +
                ", authorNickname=" + post.getAuthorNickname() +
                ", authorProfileImage=" + post.getAuthorProfileImage());

        return PostResponse.fromEntity(post);
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        // 사용자가 존재하는지 확인하고 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 게시글 엔티티 생성
        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .postImage(request.getPostImage())
                .createdAt(LocalDateTime.now())
                .likes(0)
                .views(0)
                .build();

        // 게시글 저장
        Post savedPost = postRepository.save(post);

        // 저장된 게시글에 사용자 정보 보강
        savedPost.setAuthorNickname(user.getNickname());
        savedPost.setAuthorEmail(user.getEmail());
        savedPost.setAuthorProfileImage(user.getProfileImage());

        return PostResponse.fromEntity(savedPost);
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

        Post updatedPost = postRepository.save(post);

        // 업데이트된 게시글에 사용자 정보 보강
        updatedPost = enrichPostWithUserInfo(updatedPost);

        return PostResponse.fromEntity(updatedPost);
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
        likesRepository.deleteAllByPostId(postId);

        // 관련 댓글 데이터 삭제
        commentRepository.deleteAllByPostId(postId);

        // 마지막으로 게시글 삭제
        postRepository.deleteById(postId);
    }

    /**
     * 게시글에 사용자 정보 채우기
     */
    private Post enrichPostWithUserInfo(Post post) {
        User user = userRepository.findById(post.getUserId()).orElse(null);

        if (user != null) {
            post.setAuthorNickname(user.getNickname());
            post.setAuthorEmail(user.getEmail());
            post.setAuthorProfileImage(user.getProfileImage());

            // 디버깅 로그 추가
            System.out.println("사용자 정보 보강: userId=" + user.getUserId() +
                    ", profileImage=" + user.getProfileImage());
        } else {
            System.out.println("사용자를 찾을 수 없음: userId=" + post.getUserId());
        }

        return post;
    }
}