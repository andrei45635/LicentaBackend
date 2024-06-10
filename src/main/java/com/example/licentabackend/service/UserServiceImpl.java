package com.example.licentabackend.service;

import com.example.licentabackend.dtos.UserDTO;
import com.example.licentabackend.entities.LinkedAccount;
import com.example.licentabackend.entities.User;
import com.example.licentabackend.entities.UserToken;
import com.example.licentabackend.repository.LinkedAccountRepository;
import com.example.licentabackend.repository.UserRepository;
import com.example.licentabackend.repository.UserTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private LinkedAccountRepository linkedAccountRepository;

    @Override
    @Transactional
    public User createOrUpdateUser(UserDTO spotifyUserProfile) {
        System.out.println("Display name: " + spotifyUserProfile.getDisplayName());
        User user = userRepository.findByUsername(spotifyUserProfile.getDisplayName());
        if (user == null) {
            user = new User();
            user.setUserId(spotifyUserProfile.getUserId());
            user.setUsername(spotifyUserProfile.getDisplayName());
            userRepository.save(user);

            UserToken userToken = UserToken.builder()
                    .accessToken(spotifyUserProfile.getAccessToken())
                    .expiresIn(3600)
                    .tokenType("Bearer")
                    .creationTime(LocalDateTime.now())
                    .user(user)
                    .build();
            userTokenRepository.save(userToken);

            LinkedAccount linkedAccount = LinkedAccount.builder()
                    .accessToken(spotifyUserProfile.getAccessToken())
                    .user(user)
                    .build();
            linkedAccountRepository.save(linkedAccount);
        }
        return user;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }
}
