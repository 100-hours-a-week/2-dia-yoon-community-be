package com.example.community_spring.Post.Service;

import com.example.community_spring.Post.Repository.LikesRepository;
import com.example.community_spring.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostRepository postRepository;

    /**
     * 좋아요 토글 (이미 좋아요를 누른 경우 취소, 아닌 경우 추가)
     */
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        // 게시글이 존재하는지 확인
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = likesRepository.existsByPostIdAndUserId(postId, userId);

        if (alreadyLiked) {
            // 좋아요 취소
            likesRepository.removeLike(postId, userId);
            postRepository.decrementLikes(postId);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            likesRepository.addLike(postId, userId);
            postRepository.incrementLikes(postId);
            return true; // 좋아요 추가됨
        }
    }

    /**
     * 좋아요 상태 확인
     */
    @Transactional(readOnly = true)
    public boolean getLikeStatus(Long postId, Long userId) {
        return likesRepository.existsByPostIdAndUserId(postId, userId);
    }

    /**
     * 게시글의 좋아요 수 조회
     */
    @Transactional(readOnly = true)
    public int getLikeCount(Long postId) {
        return likesRepository.countByPostId(postId);
    }
}