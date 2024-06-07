package com.example.licentabackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "audio_features")
@Table(name="AudioFeatures")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="acousticness")
    private float acousticness;

    @Column(name = "danceability")
    private float danceability;

    @Column(name = "duration_ms")
    private int durationMs;

    @Column(name = "energy")
    private float energy;

    @Column(name = "instrumentalness")
    private float instrumentalness;

    @Column(name = "key")
    private int key;

    @Column(name = "liveness")
    private float liveness;

    @Column(name = "loudness")
    private float loudness;

    @Column(name = "mode")
    private int mode;

    @Column(name = "speechiness")
    private float speechiness;

    @Column(name = "tempo")
    private float tempo;

    @Column(name = "time_signature")
    private int timeSignature;

    @Column(name = "valence")
    private float valence;

    @OneToOne
    @JoinColumn(name="track_id")
    private Track track;
}
