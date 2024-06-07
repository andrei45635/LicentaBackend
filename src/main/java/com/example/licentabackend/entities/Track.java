package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "track")
@Table(name = "Track")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "track_id", nullable = false, unique = true)
    private String trackId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "album")
    private String album;

    @Column(name = "explicit")
    private boolean explicit;
    @Column(name = "popularity")
    private int popularity;

    @ManyToMany(mappedBy = "tracks", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<Playlist> playlists;

    @OneToOne(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private AudioFeatures audioFeatures;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_artist",
            joinColumns = @JoinColumn(name = "track_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"))
    private Set<Artist> artists;
}
