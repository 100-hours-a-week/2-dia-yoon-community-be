package com.example.community_spring.Post.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikesRepository {

    private final JdbcTemplate jdbcTemplate;



    /**
     * 좋아요 추가
     */
    public void addLike(Long postId, Long userId) {
        String sql = "INSERT INTO Likes (post_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, postId, userId);
    }

    /**
     * 좋아요 취소
     */
    public void removeLike(Long postId, Long userId) {
        String sql = "DELETE FROM Likes WHERE post_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, postId, userId);
    }

    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     */
    public boolean existsByPostIdAndUserId(Long postId, Long userId) {
        String sql = "SELECT COUNT(*) FROM Likes WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId, userId);
        return count != null && count > 0;
    }

    /**
     * 특정 게시글의 좋아요 수 카운트
     */
    public int countByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM Likes WHERE post_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null ? count : 0;
    }

    // 게시물의 전체 좋아요 삭제
    public void deleteAllByPostId(Long postId) {
        String sql = "DELETE FROM Likes WHERE post_id = ?";
        int deletedRows = jdbcTemplate.update(sql, postId);
        System.out.println("삭제된 좋아요 수: " + deletedRows);
    }
}