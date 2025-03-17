package com.example.community_spring.Post.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponse {
    private List<PostResponse> posts;
    private int currentPage;
    private int totalPages;
    private int totalPosts;
    private boolean hasNext;
    private boolean hasPrevious;
}