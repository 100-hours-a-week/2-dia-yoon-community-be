package com.example.community_spring.Post.DTO.response;

import com.example.community_spring.Post.Entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String postImage;
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer views;

    // 작성자 정보
    private String authorNickname;
    private String authorEmail;
    private String authorProfileImage;

    // Post 엔티티를 PostResponse DTO로 변환
    public static PostResponse fromEntity(Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImage(post.getPostImage())
                .createdAt(post.getCreatedAt())
                .likes(post.getLikes())
                .views(post.getViews())
                .authorNickname(post.getAuthorNickname())
                .authorEmail(post.getAuthorEmail())
                .authorProfileImage(post.getAuthorProfileImage())
                .build();
    }
}