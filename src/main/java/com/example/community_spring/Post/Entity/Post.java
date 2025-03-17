package com.example.community_spring.Post.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String postImage;
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer views;

    // 사용자 정보를 함께 표시하기 위한 추가 필드 (조인 결과 저장용)
    private String authorNickname;
    private String authorEmail;
    private String authorProfileImage;
}