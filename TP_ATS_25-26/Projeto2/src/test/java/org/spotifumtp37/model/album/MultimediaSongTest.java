package org.spotifumtp37.model.album;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultimediaSongTest {

    private MultimediaSong song() {
        return new MultimediaSong("Video", "Artist", "Publisher", "Lyrics", "Notes", "Pop", 180, "http://video.url");
    }

    @Test
    void isMultimediaReturnsTrue() {
        assertTrue(song().isMultimedia());
    }

    @Test
    void isExplicitReturnsFalse() {
        assertFalse(song().isExplicit());
    }

    @Test
    void getVideoLink() {
        assertEquals("http://video.url", song().getVideoLink());
    }

    @Test
    void setVideoLink() {
        MultimediaSong s = song();
        s.setVideoLink("http://new.url");
        assertEquals("http://new.url", s.getVideoLink());
    }

    @Test
    void cloneProducesEqualButDistinct() {
        MultimediaSong original = song();
        MultimediaSong cloned = original.clone();
        assertNotSame(original, cloned);
        assertEquals(original.getName(), cloned.getName());
        assertEquals(original.getVideoLink(), cloned.getVideoLink());
        assertTrue(cloned.isMultimedia());
    }

    @Test
    void cloneWithNullVideoLink() {
        MultimediaSong s = new MultimediaSong("V", "A", "P", "L", "N", "Rock", 100, null);
        MultimediaSong cloned = s.clone();
        assertNull(cloned.getVideoLink());
    }

    @Test
    void toStringContainsMultimediaFlag() {
        assertTrue(song().toString().contains("multimedia=true"));
    }

    @Test
    void fieldsSetCorrectly() {
        MultimediaSong s = song();
        assertEquals("Video", s.getName());
        assertEquals("Artist", s.getArtist());
        assertEquals("Pop", s.getGenre());
        assertEquals(180, s.getDurationInSeconds());
    }
}
