package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "playlist")
@Table(name="Playlist")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", nullable = false)
    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @ToString.Exclude
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinTable(
            name="playlist_tracks",
            joinColumns = @JoinColumn(name="playlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="track_id",referencedColumnName = "id"))
    private Set<Track> tracks;
}
