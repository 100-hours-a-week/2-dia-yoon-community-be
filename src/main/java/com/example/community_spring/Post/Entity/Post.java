package com.example.community_spring.Post.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;

    private String title;
    private String content;

    @Column(name = "post_image")
    private String postImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // VARCHAR(30)으로 정의된 컬럼이므로 String으로 변경하거나 Integer로 유지하되 @Column 속성 추가
    @Column(name = "likes")
    private Integer likes;

    @Column(name = "views")
    private Integer views;

    // 나머지 필드는 그대로 유지
    @Transient
    private String authorNickname;

    @Transient
    private String authorEmail;

    @Transient
    private String authorProfileImage;
}