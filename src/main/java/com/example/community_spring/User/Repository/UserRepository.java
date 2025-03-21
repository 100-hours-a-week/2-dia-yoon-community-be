package com.example.community_spring.User.Repository;

import com.example.community_spring.User.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .userId(rs.getLong("user_id"))
            .nickname(rs.getString("nickname"))
            .email(rs.getString("email"))
            .password(rs.getString("password"))
            .profileImage(rs.getString("profile_image"))
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
            .build();

    // 모든 사용자 조회
    public List<User> findAll() {
        String sql = "SELECT user_id, nickname, email, password, profile_image, created_at, updated_at FROM user";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    // ID로 사용자 조회
    public Optional<User> findById(Long userId) {
        String sql = "SELECT user_id, nickname, email, password, profile_image, created_at, updated_at FROM user WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, userId);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 이메일로 사용자 조회
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT user_id, nickname, email, password, profile_image, created_at, updated_at FROM user WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 이메일 중복 체크
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    // 닉네임 중복 체크
    public boolean existsByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM user WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nickname);
        return count != null && count > 0;
    }

    // 사용자 저장
    public Long save(User user) {
        try {
            // 저장 직전 비밀번호 로깅
            System.out.println("===== 저장 직전 비밀번호 로깅 =====");
            System.out.println("사용자 이메일: " + user.getEmail());
            System.out.println("저장 직전 원본 비밀번호: " + user.getPassword());
            System.out.println("비밀번호 길이: " + user.getPassword().length());
            System.out.println("===================================");

            String sql = "INSERT INTO user (nickname, email, password, profile_image, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getNickname());
                ps.setString(2, user.getEmail());

                // 비밀번호 설정 직전 로깅
                System.out.println("PS에 비밀번호 설정 직전: " + user.getPassword());

                ps.setString(3, user.getPassword()); // 원본 비밀번호 그대로 사용
                ps.setString(4, user.getProfileImage());
                return ps;
            }, keyHolder);

            Long generatedId = keyHolder.getKey().longValue();

            // 저장 후 조회하여 로깅
            User savedUser = findById(generatedId).orElse(null);
            if (savedUser != null) {
                System.out.println("===== 저장 후 조회한 비밀번호 로깅 =====");
                System.out.println("사용자 ID: " + savedUser.getUserId());
                System.out.println("저장된 비밀번호: " + savedUser.getPassword());
                System.out.println("======================================");
            }

            return generatedId;
        } catch (Exception e) {
            System.err.println("사용자 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 프로필 업데이트
    public void updateProfile(Long userId, String nickname, String profileImage) {
        String sql = "UPDATE user SET nickname = ?, profile_image = ?, updated_at = NOW() WHERE user_id = ?";
        jdbcTemplate.update(sql, nickname, profileImage, userId);
    }

    // 비밀번호 업데이트
    public void updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE user SET password = ?, updated_at = NOW() WHERE user_id = ?";
        jdbcTemplate.update(sql, newPassword, userId);
    }

    // 사용자 삭제
    public void delete(Long userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}