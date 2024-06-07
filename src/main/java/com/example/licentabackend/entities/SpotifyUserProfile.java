package com.example.licentabackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpotifyUserProfile {
    private String id;
    private String country;
    private String email;
    @JsonProperty("display_name")
    private String displayName;
}
