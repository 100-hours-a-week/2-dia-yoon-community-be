package com.example.community_spring.Post.Repository;

import com.example.community_spring.Post.Entity.Comment;
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
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setCommentId(rs.getLong("comment_id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setUserId(rs.getLong("user_id"));
        comment.setContent(rs.getString("content"));

        // Timestamp를 LocalDateTime으로 변환
        Timestamp commentAtTimestamp = rs.getTimestamp("commentAt");
        if (commentAtTimestamp != null) {
            comment.setCommentAt(commentAtTimestamp.toLocalDateTime());
        }

        // User 테이블과 조인된 경우에만 가져오는 필드들
        try {
            comment.setAuthorNickname(rs.getString("nickname"));
            comment.setAuthorProfileImage(rs.getString("profile_image"));
        } catch (Exception e) {
            // 조인되지 않은 경우 무시
        }

        return comment;
    };

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT c.*, u.nickname, u.profile_image " +
                "FROM Comment c " +
                "JOIN User u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ? " +
                "ORDER BY c.commentAt ASC";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    /**
     * 댓글 ID로 조회
     */
    public Optional<Comment> findById(Long commentId) {
        String sql = "SELECT c.*, u.nickname, u.profile_image " +
                "FROM Comment c " +
                "JOIN User u ON c.user_id = u.user_id " +
                "WHERE c.comment_id = ?";
        try {
            Comment comment = jdbcTemplate.queryForObject(sql, commentRowMapper, commentId);
            return Optional.ofNullable(comment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 댓글 저장
     */
    public Long save(Comment comment) {
        String sql = "INSERT INTO Comment (post_id, user_id, content, commentAt) " +
                "VALUES (?, ?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPostId());
            ps.setLong(2, comment.getUserId());
            ps.setString(3, comment.getContent());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    /**
     * 댓글 수정
     */
    public void update(Comment comment) {
        String sql = "UPDATE Comment SET content = ? WHERE comment_id = ?";
        jdbcTemplate.update(sql, comment.getContent(), comment.getCommentId());
    }

    /**
     * 댓글 삭제
     */
    public void delete(Long commentId) {
        String sql = "DELETE FROM Comment WHERE comment_id = ?";
        jdbcTemplate.update(sql, commentId);
    }

    /**
     * 특정 게시글의 댓글 수 카운트
     */
    public int countByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM Comment WHERE post_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null ? count : 0;
    }

    // 게시물의 전체 댓글 삭제
    public void deleteAllByPostId(Long postId) {
        String sql = "DELETE FROM Comment WHERE post_id = ?";
        int deletedRows = jdbcTemplate.update(sql, postId);
        System.out.println("삭제된 좋아요 수: " + deletedRows);
    }
}