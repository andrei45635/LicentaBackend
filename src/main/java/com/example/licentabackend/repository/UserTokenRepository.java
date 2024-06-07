package com.example.licentabackend.repository;

import com.example.licentabackend.entities.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {
    //UserToken findByUserId(Integer userId);
    @Query("SELECT ut FROM user_token ut WHERE ut.user.userId = :userId")
    Optional<UserToken> findByUserId(String userId);
}
