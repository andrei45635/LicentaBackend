package com.example.licentabackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private String displayName;
    private List<ImageDTO> images;
    private String country;
    private String email;
    private String userId;
    private String accessToken;
}
