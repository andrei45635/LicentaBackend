package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "linked_account")
@Table(name = "LinkedAccount")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkedAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="access_token", nullable = false)
    private String accessToken;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
