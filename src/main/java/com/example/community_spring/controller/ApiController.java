package com.example.community_spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("백엔드 연결 성공!");
    }

    // 필요한 다른 API 엔드포인트들 추가
}