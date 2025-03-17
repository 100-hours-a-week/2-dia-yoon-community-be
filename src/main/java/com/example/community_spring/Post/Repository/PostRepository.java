package com.example.community_spring.Post.Repository;

import com.example.community_spring.Post.Entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    // Post 테이블 + User 테이블 조인 결과 매핑
    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setPostId(rs.getLong("post_id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setPostImage(rs.getString("post_image"));

        // Timestamp를 LocalDateTime으로 변환
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            post.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        post.setLikes(rs.getInt("likes"));
        post.setViews(rs.getInt("views"));

        // User 테이블과 조인된 경우에만 가져오는 필드들
        try {
            post.setAuthorNickname(rs.getString("nickname"));
            post.setAuthorEmail(rs.getString("email"));
            post.setAuthorProfileImage(rs.getString("profile_image"));
        } catch (Exception e) {
            // 조인되지 않은 경우 무시
        }

        return post;
    };

    // 모든 게시글 조회 (최신순)
    public List<Post> findAll(int limit, int offset) {
        String sql = "SELECT p.*, u.nickname, u.email, u.profile_image " +
                "FROM Post p " +
                "JOIN User u ON p.user_id = u.user_id " +
                "ORDER BY p.created_at DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, postRowMapper, limit, offset);
    }

    // 게시글 ID로 조회
    public Optional<Post> findById(Long postId) {
        String sql = "SELECT p.*, u.nickname, u.email, u.profile_image " +
                "FROM Post p " +
                "JOIN User u ON p.user_id = u.user_id " +
                "WHERE p.post_id = ?";
        try {
            Post post = jdbcTemplate.queryForObject(sql, postRowMapper, postId);
            return Optional.ofNullable(post);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 사용자 ID로 게시글 조회
    public List<Post> findByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT p.*, u.nickname, u.email, u.profile_image " +
                "FROM Post p " +
                "JOIN User u ON p.user_id = u.user_id " +
                "WHERE p.user_id = ? " +
                "ORDER BY p.created_at DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, postRowMapper, userId, limit, offset);
    }

    // 게시글 저장
    public Long save(Post post) {
        String sql = "INSERT INTO Post (user_id, title, content, post_image, created_at, likes, views) " +
                "VALUES (?, ?, ?, ?, NOW(), 0, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, post.getUserId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContent());
            ps.setString(4, post.getPostImage());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    // 게시글 업데이트
    public void update(Post post) {
        String sql = "UPDATE Post SET title = ?, content = ?, post_image = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getPostImage(), post.getPostId());
    }

    // 게시글 삭제
    public void delete(Long postId) {
        String sql = "DELETE FROM Post WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    // 게시글 조회수 증가
    public void incrementViews(Long postId) {
        String sql = "UPDATE Post SET views = views + 1 WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    // 게시글 좋아요 수 증가
    public void incrementLikes(Long postId) {
        String sql = "UPDATE Post SET likes = likes + 1 WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    // 게시글 좋아요 수 감소
    public void decrementLikes(Long postId) {
        String sql = "UPDATE Post SET likes = GREATEST(likes - 1, 0) WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    // 게시글 총 개수 조회
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Post";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // 사용자 게시글 총 개수 조회
    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM Post WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }
}