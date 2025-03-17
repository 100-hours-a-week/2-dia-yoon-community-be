package com.example.community_spring.Post.Service;

import com.example.community_spring.Post.DTO.request.CreateCommentRequest;
import com.example.community_spring.Post.DTO.request.UpdateCommentRequest;
import com.example.community_spring.Post.DTO.response.CommentResponse;
import com.example.community_spring.Post.Entity.Comment;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        // 게시글이 존재하는지 확인
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 댓글 목록 조회
        List<Comment> comments = commentRepository.findByPostId(postId);

        // Entity -> DTO 변환
        return comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 작성
     */
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
                .build();

        // 댓글 저장
        Long commentId = commentRepository.save(comment);

        // 저장된 댓글 조회
        Comment savedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 저장에 실패했습니다."));

        return CommentResponse.fromEntity(savedComment);
    }

    /**
     * 댓글 수정
     */
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
        commentRepository.update(comment);

        // 업데이트된 댓글 조회
        Comment updatedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 수정에 실패했습니다."));

        return CommentResponse.fromEntity(updatedComment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 댓글이 존재하는지 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 댓글 작성자와 요청자가 일치하는지 확인
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.delete(commentId);
    }
}