package com.example.community_spring.User.Repository;

import com.example.community_spring.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname, u.profileImage = :profileImage, u.updatedAt = CURRENT_TIMESTAMP WHERE u.userId = :userId")
    void updateProfile(Long userId, String nickname, String profileImage);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword, u.updatedAt = CURRENT_TIMESTAMP WHERE u.userId = :userId")
    void updatePassword(Long userId, String newPassword);
}