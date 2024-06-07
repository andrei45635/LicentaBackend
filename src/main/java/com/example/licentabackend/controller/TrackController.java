package com.example.licentabackend.controller;

import com.example.licentabackend.dtos.PlaylistDTO;
import com.example.licentabackend.dtos.TrackDTO;
import com.example.licentabackend.entities.RecommendationRequest;
import com.example.licentabackend.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.net.URI;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/spotify")
public class TrackController {
    private final TrackService trackService;
    private static final URI redirectURI = SpotifyHttpManager.makeUri("http://localhost:8080/auth/spotify/get-user-code");
    private String code = "";
    private static final String clientId = "a43ef21f342246aab5ccbd5a7447eab9";
    private static final String clientSecret = "2625f2d2090640508b6a33e9d438b0c6";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectURI)
            .build();

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/get-user-playlists")
    public PlaylistSimplified[] getCurrentUserPlaylists(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.substring("Bearer ".length());
        return trackService.getCurrentUserPlaylists(accessToken);
    }

    @GetMapping("/track-suggestions")
    public List<TrackDTO> getTrack(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("query") String query) {
        String accessToken = authorizationHeader.substring("Bearer ".length());
        return trackService.getTracks(accessToken, query);
    }

    @PostMapping("/get-suggestions-similarity")
    public PlaylistDTO getSuggestionsSimilarity(@RequestBody Map<String, List<String>> suggestions) {
        return trackService.getSuggestionsSimilarity(suggestions);
    }

    @PostMapping("/get-suggestions-emotions-eng")
    public PlaylistDTO getSuggestionsEmotionsEnglish(@RequestBody Map<String, List<String>> suggestions) {
        return trackService.getSuggestionsEmotionsEnglish(suggestions);
    }
}
