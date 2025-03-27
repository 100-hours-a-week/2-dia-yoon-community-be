package com.example.community_spring.Post.Repository;

import com.example.community_spring.Post.Entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    int countByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteAllByPostId(Long postId);
}