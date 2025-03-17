package com.example.community_spring.Post.DTO.response;

import com.example.community_spring.Post.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime commentAt;
    private String authorNickname;
    private String authorProfileImage;

    // Comment 엔티티를 CommentResponse DTO로 변환
    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .commentAt(comment.getCommentAt())
                .authorNickname(comment.getAuthorNickname())
                .authorProfileImage(comment.getAuthorProfileImage())
                .build();
    }
}