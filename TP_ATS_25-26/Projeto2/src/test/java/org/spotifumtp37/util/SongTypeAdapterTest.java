package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.album.ExplicitSong;
import org.spotifumtp37.model.album.MultimediaSong;
import org.spotifumtp37.model.album.Song;

import static org.junit.jupiter.api.Assertions.*;

class SongTypeAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Song.class, new SongTypeAdapter())
            .create();

    @Test
    void basicSongRoundTrip() {
        Song song = new Song("Title", "Artist", "Publisher", "Lyrics", "Notes", "Rock", 200);
        String json = gson.toJson(song, Song.class);
        Song restored = gson.fromJson(json, Song.class);
        assertEquals(song.getName(), restored.getName());
        assertEquals(song.getArtist(), restored.getArtist());
        assertEquals(song.getGenre(), restored.getGenre());
        assertEquals(song.getDurationInSeconds(), restored.getDurationInSeconds());
    }

    @Test
    void explicitSongRoundTrip() {
        ExplicitSong es = new ExplicitSong("Explicit", "Art", "Pub", "L", "N", "HipHop", 210);
        String json = gson.toJson(es, Song.class);
        Song restored = gson.fromJson(json, Song.class);
        assertInstanceOf(ExplicitSong.class, restored);
        assertEquals("Explicit", restored.getName());
        assertTrue(restored.isExplicit());
    }

    @Test
    void multimediaSongDeserializeWithVideoLink() {
        String json = "{\"name\":\"Vid\",\"artist\":\"Art\",\"genre\":\"Pop\",\"multimedia\":true,\"videoLink\":\"http://video.url\"}";
        Song restored = gson.fromJson(json, Song.class);
        assertInstanceOf(MultimediaSong.class, restored);
        assertEquals("http://video.url", ((MultimediaSong) restored).getVideoLink());
        assertEquals("Vid", restored.getName());
    }

    @Test
    void multimediaSongDeserializeWithoutVideoLink() {
        String json = "{\"name\":\"NoLink\",\"artist\":\"Art\",\"genre\":\"Jazz\",\"multimedia\":true}";
        Song restored = gson.fromJson(json, Song.class);
        assertInstanceOf(MultimediaSong.class, restored);
        assertNull(((MultimediaSong) restored).getVideoLink());
    }

    @Test
    void basicSongIsNotExplicitOrMultimedia() {
        Song song = new Song("Plain", "Art", "Pub", "L", "N", "Rock", 180);
        String json = gson.toJson(song, Song.class);
        Song restored = gson.fromJson(json, Song.class);
        assertFalse(restored.isExplicit());
        assertFalse(restored.isMultimedia());
    }
}
