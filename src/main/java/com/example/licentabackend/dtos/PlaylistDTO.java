package com.example.licentabackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaylistDTO {
    private String id;
    private String name;
    private String uri;
    private String description;
}
