package com.example.community_spring.User.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 모든 사용자 조회
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT user_id, nickname, email, profile_image FROM users";
        return jdbcTemplate.queryForList(sql);
    }

    // ID로 사용자 조회
    public Map<String, Object> findById(Long userId) {
        String sql = "SELECT user_id, nickname, email, profile_image FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (Exception e) {
            return null;
        }
    }

    // 이메일로 사용자 조회
    public Map<String, Object> findByEmail(String email) {
        String sql = "SELECT user_id, nickname, email, profile_image FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForMap(sql, email);
        } catch (Exception e) {
            return null;
        }
    }

    // DB 연결 테스트
    public boolean testConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return result != null && result == 1;
        } catch (Exception e) {
            return false;
        }
    }
}