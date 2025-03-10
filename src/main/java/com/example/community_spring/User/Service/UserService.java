package com.example.community_spring.User.Service;

import com.example.community_spring.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 모든 사용자 조회
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll();
    }

    // ID로 사용자 조회
    public Map<String, Object> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // 이메일로 사용자 조회
    public Map<String, Object> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // DB 연결 테스트
    public String testDatabaseConnection() {
        boolean isConnected = userRepository.testConnection();
        if (isConnected) {
            return "데이터베이스 연결 성공!";
        } else {
            return "데이터베이스 연결 실패!";
        }
    }
}