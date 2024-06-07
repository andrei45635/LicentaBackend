package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "artist")
@Table(name = "Artist")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="artist_id", nullable = false, unique = true)
    private String artistId;

    @Column(name="popularity", nullable = false)
    private int popularity;

    @Column(name = "followers")
    private int followers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "artist_genre",
            joinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id"))
    private Set<Genre> genres;

    @ManyToMany(mappedBy="artists", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<Track> tracks;
}
