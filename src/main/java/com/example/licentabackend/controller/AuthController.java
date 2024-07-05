package com.example.licentabackend.controller;

import com.example.licentabackend.dtos.ImageDTO;
import com.example.licentabackend.dtos.UserDTO;
import com.example.licentabackend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/spotify")
public class AuthController {
    private static final URI redirectURI = SpotifyHttpManager.makeUri("http://localhost:8080/auth/spotify/get-user-code");
    private String code = "";
    private static final String clientId = "clientId";
    private static final String clientSecret = "secret";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectURI)
            .build();

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    @ResponseBody
    public String spotifyLogin() {
        AuthorizationCodeUriRequest authorizationCodeRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-read-playback-state, user-modify-playback-state," +
                        " user-read-currently-playing, app-remote-control, streaming, playlist-read-private, playlist-read-collaborative, playlist-modify-public, " +
                        "user-follow-read, user-read-playback-position, user-top-read, user-read-recently-played, user-library-modify, user-library-read")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeRequest.execute();
        return uri.toString();
    }

    @GetMapping(value = "get-user-code")
    public String getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        System.out.println("User code: " + userCode);
        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        try{
            final AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
            System.out.println("Expires in: " + credentials.getExpiresIn());
        } catch (ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("http://localhost:4200/spotify-callback");
        System.out.println("Token: " + spotifyApi.getAccessToken());
        return spotifyApi.getAccessToken();
    }

    @GetMapping("/get-access-token")
    public String getAccessToken() {
        return spotifyApi.getAccessToken();
    }

    @GetMapping(value = "get-user-info")
    public UserDTO getUserInfo() {
        final GetCurrentUsersProfileRequest getUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
        try{
            final User user = getUsersProfileRequest.execute();
            UserDTO userProfileDTO = new UserDTO();
            userProfileDTO.setDisplayName(user.getDisplayName());
            userProfileDTO.setEmail(user.getEmail());
            userProfileDTO.setCountry(String.valueOf(user.getCountry()));
            userProfileDTO.setUserId(user.getId());
            userProfileDTO.setAccessToken(spotifyApi.getAccessToken());
            userService.createOrUpdateUser(userProfileDTO);
            List<ImageDTO> imageDTOs = Arrays.stream(user.getImages())
                    .map(image -> new ImageDTO(image.getHeight(), image.getUrl(), image.getWidth()))
                    .collect(Collectors.toList());
            userProfileDTO.setImages(imageDTOs);
            return userProfileDTO;
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }
}
