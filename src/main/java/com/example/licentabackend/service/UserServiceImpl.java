package com.example.licentabackend.service;

import com.example.licentabackend.dtos.UserDTO;
import com.example.licentabackend.entities.User;
import com.example.licentabackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User createOrUpdateUser(UserDTO spotifyUserProfile) {
        System.out.println("Display name: " + spotifyUserProfile.getDisplayName());
        User user = userRepository.findByUsername(spotifyUserProfile.getDisplayName());
        if (user == null) {
            user = new User();
            user.setUserId(spotifyUserProfile.getUserId());
            user.setUsername(spotifyUserProfile.getDisplayName());
            user.setAccessToken(spotifyUserProfile.getAccessToken());
            userRepository.save(user);
        }
        return user;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }
}
