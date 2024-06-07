package com.example.licentabackend.service;

import com.example.licentabackend.dtos.PlaylistDTO;
import com.example.licentabackend.dtos.TrackDTO;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Map;

public interface TrackService {
    PlaylistSimplified[] getCurrentUserPlaylists(String accessToken);
    List<TrackDTO> getTracks(String accessToken, String trackId);
    PlaylistDTO getSuggestionsSimilarity(Map<String, List<String>> suggestions);
    TrackDTO findTrackById(String accessToken, String trackId);
    Playlist createPlaylist(String accessToken, String userId, String playlistName);
    boolean addSongsToPlaylist(String accessToken, String playlistId, List<String> tracks);
    PlaylistDTO getSuggestionsEmotionsEnglish(Map<String, List<String>> suggestions);
    TrackDTO getTracksByArtistAndName(String accessToken, String artist, String name);
}
