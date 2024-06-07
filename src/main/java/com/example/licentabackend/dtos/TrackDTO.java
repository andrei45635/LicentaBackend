package com.example.licentabackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackDTO {
    private String trackId;
    private String name;
    private String artist;
    private String album;
    private ImageDTO images;
}
