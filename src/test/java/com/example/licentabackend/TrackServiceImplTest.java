package com.example.licentabackend;

import com.example.licentabackend.dtos.TrackDTO;
import com.example.licentabackend.service.TrackServiceImpl;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TrackServiceImplTest {

    @Mock
    private SpotifyApi spotifyApi;

    @InjectMocks
    private TrackServiceImpl trackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testGetCurrentUserPlaylists_ValidAccessToken() throws IOException, SpotifyWebApiException, ParseException {
//        String dummyAccessToken = "BQBTkin2YBu9jMU9jO0hIx3YYk-f2MNDP0mWMGwrnh_7LLNozq9DlyK9TaG40Z-baxfgqYQ-BPEIi7PHlx5Q_0Rj_h_IRH3wDaSIg3rzDSZK2eDy7Jq7yMIx2PxzIv5q4TvUXDPL-yluDgkBsrYhquKsrZZZlffuwq6hKHd3C3WUGG2wevXkWU-wvz--oUIsF_J_9uvI-BEdEjn55qjaAXFjssqK0WQ1qtZgmNRgSAb6ir6YLtRPwCkynexJLjEVwQ_mTvaCFkrruvuvtIP8YM23YiZCS_lau2fC3tyo9IDAwru78A";
//        GetListOfCurrentUsersPlaylistsRequest.Builder mockRequestBuilder = mock(GetListOfCurrentUsersPlaylistsRequest.Builder.class);
//        GetListOfCurrentUsersPlaylistsRequest mockRequest = mock(GetListOfCurrentUsersPlaylistsRequest.class);
//        Paging<PlaylistSimplified> mockPaging = mock(Paging.class);
//        PlaylistSimplified playlist = new PlaylistSimplified.Builder().setName("Test Playlist").build();
//        when(mockPaging.getItems()).thenReturn(new PlaylistSimplified[]{playlist});
//        when(mockRequest.execute()).thenReturn(mockPaging);
//        when(mockRequestBuilder.build()).thenReturn(mockRequest);
//
//        when(spotifyApi.getListOfCurrentUsersPlaylists()).thenReturn(mockRequestBuilder);
//
//        PlaylistSimplified[] result = trackService.getCurrentUserPlaylists(dummyAccessToken);
//
//        assertEquals(50, result.length);
//        assertEquals("Songs similar to your taste", result[0].getName());
//    }
//
//    @Test
//    void testGetCurrentUserPlaylists_InvalidAccessToken() throws IOException, SpotifyWebApiException, ParseException {
//        GetListOfCurrentUsersPlaylistsRequest.Builder mockRequestBuilder = mock(GetListOfCurrentUsersPlaylistsRequest.Builder.class);
//        GetListOfCurrentUsersPlaylistsRequest mockRequest = mock(GetListOfCurrentUsersPlaylistsRequest.class);
//        when(mockRequest.execute()).thenThrow(new SpotifyWebApiException("Invalid access token"));
//        when(mockRequestBuilder.build()).thenReturn(mockRequest);
//
//        when(spotifyApi.getListOfCurrentUsersPlaylists()).thenReturn(mockRequestBuilder);
//
//        PlaylistSimplified[] result = trackService.getCurrentUserPlaylists("invalidAccessToken");
//
//        assertEquals(0, result.length);
//    }
//
//    @Test
//    void testGetTracks_ValidAccessToken() throws IOException, SpotifyWebApiException, ParseException {
//        SearchTracksRequest.Builder mockRequestBuilder = mock(SearchTracksRequest.Builder.class);
//        SearchTracksRequest mockRequest = mock(SearchTracksRequest.class);
//        Paging<Track> mockPaging = mock(Paging.class);
//        Track track = new Track.Builder().setName("Speak To Me - 2011 Remastered Version").build();
//        when(mockPaging.getItems()).thenReturn(new Track[]{track});
//        when(mockRequest.execute()).thenReturn(mockPaging);
//        when(mockRequestBuilder.build()).thenReturn(mockRequest);
//
//        when(spotifyApi.searchTracks(anyString())).thenReturn(mockRequestBuilder);
//
//        List<TrackDTO> result = trackService.getTracks("BQBTkin2YBu9jMU9jO0hIx3YYk-f2MNDP0mWMGwrnh_7LLNozq9DlyK9TaG40Z-baxfgqYQ-BPEIi7PHlx5Q_0Rj_h_IRH3wDaSIg3rzDSZK2eDy7Jq7yMIx2PxzIv5q4TvUXDPL-yluDgkBsrYhquKsrZZZlffuwq6hKHd3C3WUGG2wevXkWU-wvz--oUIsF_J_9uvI-BEdEjn55qjaAXFjssqK0WQ1qtZgmNRgSAb6ir6YLtRPwCkynexJLjEVwQ_mTvaCFkrruvuvtIP8YM23YiZCS_lau2fC3tyo9IDAwru78A", "ABBA");
//
//        assertEquals(5, result.size());
//        assertEquals("Lay All Your Love On Me", result.get(0).getName());
//    }
//
//    @Test
//    void testGetTracks_InvalidAccessToken() throws IOException, SpotifyWebApiException, ParseException {
//        SearchTracksRequest.Builder mockRequestBuilder = mock(SearchTracksRequest.Builder.class);
//        SearchTracksRequest mockRequest = mock(SearchTracksRequest.class);
//        when(mockRequest.execute()).thenThrow(new SpotifyWebApiException("Invalid access token"));
//        when(mockRequestBuilder.build()).thenReturn(mockRequest);
//
//        when(spotifyApi.searchTracks(anyString())).thenReturn(mockRequestBuilder);
//
//        List<TrackDTO> result = trackService.getTracks("invalidAccessToken", "testQuery");
//
//        assertNull(result);
//    }
//
//    @Test
//    void testFindTrackById_ValidTrackId() throws IOException, SpotifyWebApiException, ParseException {
//        SearchTracksRequest.Builder mockRequestBuilder = mock(SearchTracksRequest.Builder.class);
//        SearchTracksRequest mockRequest = mock(SearchTracksRequest.class);
//        Paging<Track> mockPaging = mock(Paging.class);
//        Track track = new Track.Builder().setId("3Llmkp7FprXaaLwTjDPjX2").setName("Faster n Harder (w/ Tara Yummy)").build();
//        when(mockPaging.getItems()).thenReturn(new Track[]{track});
//        when(mockRequest.execute()).thenReturn(mockPaging);
//        when(mockRequestBuilder.build()).thenReturn(mockRequest);
//
//        when(spotifyApi.searchTracks(anyString())).thenReturn(mockRequestBuilder);
//
//        TrackDTO result = trackService.findTrackById("BQBTkin2YBu9jMU9jO0hIx3YYk-f2MNDP0mWMGwrnh_7LLNozq9DlyK9TaG40Z-baxfgqYQ-BPEIi7PHlx5Q_0Rj_h_IRH3wDaSIg3rzDSZK2eDy7Jq7yMIx2PxzIv5q4TvUXDPL-yluDgkBsrYhquKsrZZZlffuwq6hKHd3C3WUGG2wevXkWU-wvz--oUIsF_J_9uvI-BEdEjn55qjaAXFjssqK0WQ1qtZgmNRgSAb6ir6YLtRPwCkynexJLjEVwQ_mTvaCFkrruvuvtIP8YM23YiZCS_lau2fC3tyo9IDAwru78A", "3Llmkp7FprXaaLwTjDPjX2");
//
//        assertEquals("Faster n Harder (w/ Tara Yummy)", result.getName());
//        assertEquals("3Llmkp7FprXaaLwTjDPjX2", result.getTrackId());
//    }
}
