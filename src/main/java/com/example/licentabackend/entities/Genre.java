package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "genre")
@Table(name = "Genre")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<Artist> artists;
}

