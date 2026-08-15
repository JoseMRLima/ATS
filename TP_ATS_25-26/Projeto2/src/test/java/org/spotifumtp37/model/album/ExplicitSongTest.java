package org.spotifumtp37.model.album;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExplicitSongTest {

    private ExplicitSong song() {
        return new ExplicitSong("Explicit", "Artist", "Publisher", "Lyrics", "Notes", "Rock", 200);
    }

    @Test
    void isExplicitReturnsTrue() {
        assertTrue(song().isExplicit());
    }

    @Test
    void isMultimediaReturnsFalse() {
        assertFalse(song().isMultimedia());
    }

    @Test
    void cloneProducesEqualButDistinct() {
        ExplicitSong original = song();
        ExplicitSong cloned = original.clone();
        assertNotSame(original, cloned);
        assertEquals(original.getName(), cloned.getName());
        assertEquals(original.getArtist(), cloned.getArtist());
        assertTrue(cloned.isExplicit());
    }

    @Test
    void toStringContainsExplicitFlag() {
        assertTrue(song().toString().contains("explicit=true"));
    }

    @Test
    void fieldsSetCorrectly() {
        ExplicitSong s = song();
        assertEquals("Explicit", s.getName());
        assertEquals("Artist", s.getArtist());
        assertEquals("Rock", s.getGenre());
        assertEquals(200, s.getDurationInSeconds());
    }
}
