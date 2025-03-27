package com.example.community_spring.Post.Repository;

import com.example.community_spring.Post.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 페이징을 사용한 사용자 ID로 게시글 조회
    Page<Post> findByUserId(Long userId, Pageable pageable);

    // 게시글 조회수 증가
    @Modifying
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.postId = :postId")
    void incrementViews(@Param("postId") Long postId);

    // 게시글 좋아요 수 증가
    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + 1 WHERE p.postId = :postId")
    void incrementLikes(@Param("postId") Long postId);

    // 게시글 좋아요 수 감소
    @Modifying
    @Query("UPDATE Post p SET p.likes = GREATEST(p.likes - 1, 0) WHERE p.postId = :postId")
    void decrementLikes(@Param("postId") Long postId);
}