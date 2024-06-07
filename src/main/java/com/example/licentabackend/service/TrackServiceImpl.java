package com.example.licentabackend.service;

import com.example.licentabackend.dtos.ImageDTO;
import com.example.licentabackend.dtos.PlaylistDTO;
import com.example.licentabackend.dtos.TrackDTO;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackServiceImpl implements TrackService {
    private static final URI redirectURI = SpotifyHttpManager.makeUri("http://localhost:8080/auth/spotify/get-user-code");
    private String code = "";
    private static final String clientId = "a43ef21f342246aab5ccbd5a7447eab9";
    private static final String clientSecret = "2625f2d2090640508b6a33e9d438b0c6";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectURI)
            .build();

    @Override
    public PlaylistSimplified[] getCurrentUserPlaylists(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        final GetListOfCurrentUsersPlaylistsRequest request = spotifyApi.getListOfCurrentUsersPlaylists().build();
        try {
            Paging<PlaylistSimplified> playlistPaging = request.execute();
            return playlistPaging.getItems();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new PlaylistSimplified[0];
    }

    @Override
    public List<TrackDTO> getTracks(String accessToken, String query) {
        spotifyApi.setAccessToken(accessToken);
        try {
            final SearchTracksRequest request = spotifyApi.searchTracks(query)
                    .market(CountryCode.US)
                    .limit(5)
                    .build();
            Paging<Track> foundTracks = request.execute();
            List<TrackDTO> tracks = Arrays.stream(foundTracks.getItems())
                    .map(track -> {
                        TrackDTO trackDTO = new TrackDTO();
                        trackDTO.setTrackId(track.getId());
                        trackDTO.setName(track.getName());
                        trackDTO.setArtist(track.getArtists()[0].getName());
                        trackDTO.setAlbum(track.getAlbum().getName());
                        Image image = track.getAlbum().getImages()[0];
                        ImageDTO imageDTO = new ImageDTO(image.getHeight(), image.getUrl(), image.getWidth());
                        trackDTO.setImages(imageDTO);
                        return trackDTO;
                    })
                    .toList();
            System.out.println("Tracks: " + tracks);
            return tracks;
        } catch (Exception e) {
            System.out.println("Error in getTracks method in TrackServiceImpl line 48: " + e.getMessage());
        }
        return null;
    }

    @Override
    public PlaylistDTO getSuggestionsSimilarity(Map<String, List<String>> suggestions) {
        List<String> cosineIds = suggestions.get("cosine");
        List<String> euclideanIds = suggestions.get("euclidean");
        List<String> pearsonIds = suggestions.get("pearson");
        List<String> accessToken = suggestions.get("access_token");

        List<String> allIds = new ArrayList<>();
        allIds.addAll(cosineIds);
        allIds.addAll(euclideanIds);
        allIds.addAll(pearsonIds);

        Playlist playlist = createPlaylist(accessToken.get(0), getCurrentUserId(accessToken.get(0)), "Songs similar to your taste");
        addSongsToPlaylist(accessToken.get(0), playlist.getId(), allIds);

        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setName(playlist.getName());
        playlistDTO.setId(playlist.getId());
        playlistDTO.setUri(playlist.getUri());
        playlistDTO.setDescription(playlist.getDescription());

        return playlistDTO;
    }

    private String getCurrentUserId(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        try {
            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                    .build();
            User user = getCurrentUsersProfileRequest.execute();
            return user.getId();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error retrieving user profile: " + e.getMessage());
        }
        return null;
    }

    @Override
    public TrackDTO findTrackById(String accessToken, String trackId) {
        spotifyApi.setAccessToken(accessToken);
        try {
            final SearchTracksRequest request = spotifyApi.searchTracks(trackId)
                    .market(CountryCode.US)
                    .limit(1)
                    .build();
            Track foundTrack = request.execute().getItems()[0];
            System.out.println("Found track: " + foundTrack.getName());
            TrackDTO trackDTO = new TrackDTO();
            trackDTO.setTrackId(foundTrack.getId());
            trackDTO.setName(foundTrack.getName());
            trackDTO.setAlbum(foundTrack.getAlbum().getName());
            trackDTO.setArtist(foundTrack.getArtists()[0].getName());
            trackDTO.setImages(new ImageDTO(foundTrack.getAlbum().getImages()[0].getHeight(),
                    foundTrack.getAlbum().getImages()[0].getUrl(),
                    foundTrack.getAlbum().getImages()[0].getWidth()));
            return trackDTO;
        } catch (Exception e) {
            System.out.println("Error in getTracks method in TrackServiceImpl line 48: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Playlist createPlaylist(String accessToken, String userId, String playlistName) {
        spotifyApi.setAccessToken(accessToken);
        try {
            final CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(getCurrentUserId(accessToken), "Songs similar to your taste")
                    .collaborative(false)
                    .public_(true)
                    .description("Songs similar to your taste. Enjoy!")
                    .build();
            Playlist playlist = createPlaylistRequest.execute();
            System.out.println("Playlist created: " + playlist.getName());
            return playlist;
        } catch (Exception e) {
            System.out.println("Error in getTracks method in TrackServiceImpl line 48: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean addSongsToPlaylist(String accessToken, String playlistId, List<String> tracks) {
        spotifyApi.setAccessToken(accessToken);
        try {
            String[] uris = tracks.stream().map(id -> "spotify:track:" + id).toArray(String[]::new);
            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(playlistId, uris).build();
            addItemsToPlaylistRequest.execute();
            return true;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error adding tracks to playlist: " + e.getMessage());
        }
        return false;
    }

    @Override
    public PlaylistDTO getSuggestionsEmotionsEnglish(Map<String, List<String>> suggestions) {
        List<String> songs = suggestions.get("songs");
        String predictedEmotion = suggestions.get("emotion").get(0);
        String accessToken = suggestions.get("access_token").get(0);
        System.out.println("Songs: " + songs);
        System.out.println("Predicted emotion: " + predictedEmotion);
        List<TrackDTO> tracks = new ArrayList<>();
        for(String song: songs) {
            String[] parts = song.split(" - ");
            String artist = parts[0];
            String track  = parts[1];
            String score  = parts[2];
            System.out.println("Artist track: " + artist + " - " + track + " - " + score);
            TrackDTO foundTrack = getTracksByArtistAndName(accessToken, artist, track);
            if (foundTrack != null) {
                tracks.add(foundTrack);
                System.out.println("Found track: " + foundTrack);
            } else {
                System.out.println("Track not found for artist: " + artist + ", track: " + track);
            }
        }
        System.out.println("Tracks: " + tracks);
        List<String> allIds = tracks.stream().map(TrackDTO::getTrackId).collect(Collectors.toList());
        Playlist playlist = createPlaylist(accessToken, getCurrentUserId(accessToken), "Songs similar to your taste");
        addSongsToPlaylist(accessToken, playlist.getId(), allIds);
        System.out.println("Playlist ID: " + playlist.getId());
        System.out.println("Playlist :" + playlist);
        System.out.println("Songs in the playlist: " + playlist.getTracks());
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setName(playlist.getName());
        playlistDTO.setId(playlist.getId());
        playlistDTO.setUri(playlist.getUri());
        playlistDTO.setDescription(playlist.getDescription());
        return playlistDTO;
    }

    @Override
    public TrackDTO getTracksByArtistAndName(String accessToken, String artist, String trackName) {
        spotifyApi.setAccessToken(accessToken);
        try {
            String query = "artist:" + URLEncoder.encode(artist, StandardCharsets.UTF_8) + " track:" + URLEncoder.encode(trackName, StandardCharsets.UTF_8);
            final SearchTracksRequest request = spotifyApi.searchTracks(query)
                    .market(CountryCode.US)
                    .limit(1)
                    .build();
            Paging<Track> foundTracks = request.execute();
            Track[] items = foundTracks.getItems();
            if (items.length == 0) {
                System.out.println("No tracks found for query: " + query);
                return null;
            }
            System.out.println("Found item: " + Arrays.toString(items));
            TrackDTO track = new TrackDTO();
            track.setName(items[0].getName());
            track.setArtist(items[0].getArtists()[0].getName());
            track.setAlbum(items[0].getAlbum().getName());
            track.setTrackId(items[0].getId());
            Image image = items[0].getAlbum().getImages()[0];
            ImageDTO imageDTO = new ImageDTO(image.getHeight(), image.getUrl(), image.getWidth());
            track.setImages(imageDTO);
            return track;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error in getTracksByArtistAndName method: " + e.getMessage());
        }
        return null;
    }
}
