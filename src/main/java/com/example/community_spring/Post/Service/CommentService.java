package com.example.community_spring.Post.Service;

import com.example.community_spring.Post.DTO.request.CreateCommentRequest;
import com.example.community_spring.Post.DTO.request.UpdateCommentRequest;
import com.example.community_spring.Post.DTO.response.CommentResponse;
import com.example.community_spring.Post.Entity.Comment;
import com.example.community_spring.Post.Repository.CommentRepository;
import com.example.community_spring.Post.Repository.PostRepository;
import com.example.community_spring.User.Entity.User;
import com.example.community_spring.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        // 게시글이 존재하는지 확인
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 댓글 목록 조회
        List<Comment> comments = commentRepository.findByPostIdOrderByCommentAtAsc(postId);

        // 각 댓글에 최신 사용자 정보 보강
        List<CommentResponse> result = new ArrayList<>();

        for (Comment comment : comments) {
            User user = userRepository.findById(comment.getUserId()).orElse(null);

            if (user != null) {
                comment.setAuthorNickname(user.getNickname());
                comment.setAuthorProfileImage(user.getProfileImage());

                System.out.println("댓글 사용자 정보: userId=" + user.getUserId() +
                        ", profileImage=" + user.getProfileImage());
            } else {
                System.out.println("댓글 작성자를 찾을 수 없음: userId=" + comment.getUserId());
            }

            result.add(CommentResponse.fromEntity(comment));
        }

        return result;
    }

    @Transactional
    public CommentResponse createComment(Long postId, Long userId, CreateCommentRequest request) {
        // 게시글이 존재하는지 확인
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 사용자가 존재하는지 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(request.getContent())
                .commentAt(LocalDateTime.now()) // 현재 시간 설정
                .build();

        // 댓글 저장 (반환 타입 변경)
        Comment savedComment = commentRepository.save(comment); // JPA는 저장 후 엔티티를 반환

        return CommentResponse.fromEntity(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, Long userId, UpdateCommentRequest request) {
        // 댓글이 존재하는지 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 댓글 작성자와 요청자가 일치하는지 확인
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        // 댓글 내용 업데이트
        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment); // update -> save

        return CommentResponse.fromEntity(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 댓글이 존재하는지 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 댓글 작성자와 요청자가 일치하는지 확인
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제 (메소드 변경)
        commentRepository.deleteById(commentId); // delete(commentId) -> deleteById(commentId)
    }
}