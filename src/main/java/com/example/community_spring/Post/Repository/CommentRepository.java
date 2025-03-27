package com.example.community_spring.Post.Repository;

import com.example.community_spring.Post.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCommentAtAsc(Long postId);

    int countByPostId(Long postId);

    void deleteAllByPostId(Long postId);
}