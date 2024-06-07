package com.example.licentabackend.repository;

import com.example.licentabackend.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Integer> {

}
