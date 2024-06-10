package com.example.licentabackend.service;

import com.example.licentabackend.dtos.UserDTO;
import com.example.licentabackend.entities.User;

public interface UserService {
    User createOrUpdateUser(UserDTO spotifyUserProfile);
}
