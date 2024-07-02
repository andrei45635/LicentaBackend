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
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;
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

        String playlistName = "Diverse Similarity Playlist";
        String playlistDescription = "A unique blend of tracks selected using cosine, Euclidean, and Pearson similarity measures. Enjoy a diverse listening experience!";

        Playlist playlist = createPlaylist(accessToken.get(0), getCurrentUserId(accessToken.get(0)), playlistName, playlistDescription);
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
    public TrackDTO findTrackById(String accessToken, String trackId) throws SpotifyWebApiException {
        spotifyApi.setAccessToken(accessToken);
        try {
            final GetTrackRequest request = spotifyApi.getTrack(trackId)
                    .market(CountryCode.US)
                    .build();
            Track track = request.execute();
            if(!Objects.isNull(track)) {
                TrackDTO trackDTO = new TrackDTO();
                trackDTO.setTrackId(track.getId());
                trackDTO.setName(track.getName());
                trackDTO.setAlbum(track.getAlbum().getName());
                trackDTO.setArtist(track.getArtists()[0].getName());
                trackDTO.setImages(new ImageDTO(track.getAlbum().getImages()[0].getHeight(),
                        track.getAlbum().getImages()[0].getUrl(),
                        track.getAlbum().getImages()[0].getWidth()));
                return trackDTO;
            } else {
                return null;
            }
        } catch (SpotifyWebApiException | IOException | ParseException exception) {
            throw new SpotifyWebApiException("Track with ID " + trackId + " not found");
        }
    }

    @Override
    public Playlist createPlaylist(String accessToken, String userId, String playlistName, String playlistDescription) {
        spotifyApi.setAccessToken(accessToken);
        try {
            final CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(getCurrentUserId(accessToken), playlistName)
                    .collaborative(false)
                    .public_(true)
                    .description(playlistDescription)
                    .build();
            Playlist playlist = createPlaylistRequest.execute();
            System.out.println("Playlist created: " + playlist.getName());
            return playlist;
        } catch (Exception e) {
            System.out.println("Error in getTracks method in TrackServiceImpl line 48: " + e.getMessage());
        }
        return null;
    }

    private String generateTitleBasedOnEmotionEnglish(String emotion) {
        return switch (emotion.toLowerCase()) {
            case "amazement" -> "Amazement Tunes";
            case "calmness" -> "Calm Vibes";
            case "joyful activation" -> "Joyful Beats";
            case "nostalgia" -> "Nostalgic Hits";
            case "power" -> "Power Tracks";
            case "sadness" -> "Sad Songs";
            case "solemnity" -> "Solemn Melodies";
            case "tenderness" -> "Tender Tunes";
            case "tension" -> "Tense Beats";
            default -> "Emotion-based Playlist";
        };
    }

    private String generateDescriptionBasedOnEmotionEnglish(String emotion) {
        return switch (emotion.toLowerCase()) {
            case "amazement" -> "Experience awe with these amazing tracks!";
            case "calmness" -> "Relax and unwind with these calming songs.";
            case "joyful activation" -> "Feel the joy with these upbeat tracks!";
            case "nostalgia" -> "Take a trip down memory lane with these nostalgic hits.";
            case "power" -> "Feel empowered with these powerful songs.";
            case "sadness" -> "Embrace your sadness with these melancholic tunes.";
            case "solemnity" -> "Reflect with these solemn melodies.";
            case "tenderness" -> "Feel the warmth with these tender tracks.";
            case "tension" -> "Get through the tension with these intense beats.";
            default -> "A playlist curated based on your current mood.";
        };
    }

    private String generateTitleBasedOnEmotionRomanian(String emotion) {
        return switch (emotion.toLowerCase()) {
            case "tristețe" -> "Cântece Triste";
            case "surpriză" -> "Melodii Surprinzătoare";
            case "frică" -> "Piese de Frică";
            case "furie" -> "Cântece de Furie";
            case "neutru" -> "Cântece Neutre";
            case "încredere" -> "Piese de Încredere";
            case "bucurie" -> "Cântece de Bucurie";
            default -> "Playlist pe bază de emoții";
        };
    }

    private String generateDescriptionBasedOnEmotionRomanian(String emotion) {
        return switch (emotion.toLowerCase()) {
            case "tristețe" -> "Însoțește-ți tristețea cu aceste melodii melancolice.";
            case "surpriză" -> "Bucură-te de aceste piese surprinzătoare!";
            case "frică" -> "Melodii care te fac să simți frică.";
            case "furie" -> "Scoate-ți furia cu aceste cântece intense.";
            case "neutru" -> "O selecție de melodii neutre pentru orice stare.";
            case "încredere" -> "Simte încrederea cu aceste piese puternice.";
            case "bucurie" -> "Umple-te de bucurie cu aceste melodii vesele.";
            default -> "Un playlist creat pe baza stării tale actuale.";
        };
    }

    @Override
    public boolean addSongsToPlaylist(String accessToken, String playlistId, List<String> tracks) {
        spotifyApi.setAccessToken(accessToken);
        try {
            System.out.println("tracks: " + tracks);
            String[] uris = tracks.stream().map(id -> "spotify:track:" + id).toArray(String[]::new);
            System.out.println("URIs: " + Arrays.toString(uris));
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
            TrackDTO foundTrack = getTracksByArtistAndName(accessToken, artist, track);
            if (foundTrack != null) {
                tracks.add(foundTrack);
                System.out.println("Found track: " + foundTrack);
            } else {
                System.out.println("Track not found for artist: " + artist + ", track: " + track);
            }
        }
        System.out.println("Tracks: " + tracks);
        String playlistTitle = generateTitleBasedOnEmotionEnglish(predictedEmotion);
        String playlistDescription = generateDescriptionBasedOnEmotionEnglish(predictedEmotion);
        List<String> allIds = tracks.stream().map(TrackDTO::getTrackId).collect(Collectors.toList());
        Playlist playlist = createPlaylist(accessToken, getCurrentUserId(accessToken), playlistTitle, playlistDescription);
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

    @Override
    public PlaylistDTO getSuggestionsEmotionsRomanian(Map<String, List<String>> suggestions) {
        System.out.println("Suggestions: " + suggestions);
        List<String> songs = suggestions.get("songs");
        String predictedEmotion = suggestions.get("emotion").get(0);
        String accessToken = suggestions.get("access_token").get(0);
        System.out.println("Songs: " + songs);
        List<TrackDTO> tracks = new ArrayList<>();
        for(String song: songs) {
            String[] parts = song.split(" - ");
            String artist = parts[0];
            String track  = parts[1];
            TrackDTO foundTrack = getTracksByArtistAndName(accessToken, artist, track);
            if (foundTrack != null) {
                tracks.add(foundTrack);
                System.out.println("Found track: " + foundTrack);
            } else {
                System.out.println("Track not found for artist: " + artist + ", track: " + track);
            }
        }
        System.out.println("Tracks: " + tracks);
        String playlistTitle = generateTitleBasedOnEmotionRomanian(predictedEmotion);
        String playlistDescription = generateDescriptionBasedOnEmotionRomanian(predictedEmotion);
        List<String> allIds = tracks.stream().map(TrackDTO::getTrackId).collect(Collectors.toList());
        Playlist playlist = createPlaylist(accessToken, getCurrentUserId(accessToken), playlistTitle, playlistDescription);
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
}