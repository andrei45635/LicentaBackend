package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "\"user\"")
@Table(name="\"user\"")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="username", length=55, unique = true, nullable = false)
    private String username;

    @Column(name="access_token", length=255, nullable = false, unique = true)
    private String accessToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Playlist> playlists;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private LinkedAccount linkedAccount;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private UserToken spotifyToken;
}
