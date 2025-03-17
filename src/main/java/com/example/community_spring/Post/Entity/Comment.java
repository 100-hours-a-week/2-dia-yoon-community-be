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
public class Comment {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime commentAt;

    // 사용자 정보를 함께 표시하기 위한 추가 필드 (조인 결과 저장용)
    private String authorNickname;
    private String authorProfileImage;
}