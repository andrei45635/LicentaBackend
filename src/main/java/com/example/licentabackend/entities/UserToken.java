package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "user_token")
@Table(name = "UserToken")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "access_token",length = 2048, nullable = false)
    private String accessToken;
    @Column(name = "refresh_token",length = 2048, nullable = false)
    private String refreshToken;
    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;
    @Column(name = "token_type", nullable = false)
    private String tokenType;
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
