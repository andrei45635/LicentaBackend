package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private LinkedAccount linkedAccount;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private UserToken spotifyToken;
}
