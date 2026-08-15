package org.spotifumtp37.model.album;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SongTest {

    private Song song;
    private Song explicitSong;
    private MultimediaSong multimediaSong;

    @BeforeEach
    void setUp() {
        song = new Song("Halo", "Beyonce", "Columbia", "Remember those walls...", "C G Am F", "Pop", 240);
        explicitSong = new ExplicitSong("Explicit Song", "Eminem", "Interscope", "Explicit lyrics here", "Dm G C", "Hip Hop", 180);
        multimediaSong = new MultimediaSong("Video Song", "Madonna", "Warner", "Video song lyrics", "A E F", "Pop", 210, "https://example.com/video");
    }

    @Test
    void testDefaultConstructor() {
        Song defaultSong = new Song();
        assertEquals("", defaultSong.getName());
        assertEquals("", defaultSong.getArtist());
        assertEquals("", defaultSong.getPublisher());
        assertEquals("", defaultSong.getLyrics());
        assertEquals("", defaultSong.getMusicalNotes());
        assertEquals("", defaultSong.getGenre());
        assertEquals(0, defaultSong.getDurationInSeconds());
        assertEquals(0, defaultSong.getTimesPlayed());
    }

    @Test
    void testGetName() {
        assertEquals("Halo", song.getName());
        assertEquals("Explicit Song", explicitSong.getName());
        assertEquals("Video Song", multimediaSong.getName());
    }

    @Test
    void testSetName() {
        song.setName("Single Ladies");
        assertEquals("Single Ladies", song.getName());
        song.setName("");
        assertEquals("", song.getName());
        song.setName(null);
        assertNull(song.getName());
    }

    @Test
    void testGetArtist() {
        assertEquals("Beyonce", song.getArtist());
        assertEquals("Eminem", explicitSong.getArtist());
        assertEquals("Madonna", multimediaSong.getArtist());
    }

    @Test
    void testSetArtist() {
        song.setArtist("Adele");
        assertEquals("Adele", song.getArtist());
        song.setArtist(null);
        assertNull(song.getArtist());
    }

    @Test
    void testGetPublisher() {
        assertEquals("Columbia", song.getPublisher());
    }

    @Test
    void testSetPublisher() {
        song.setPublisher("Sony");
        assertEquals("Sony", song.getPublisher());
    }

    @Test
    void testGetLyrics() {
        assertEquals("Remember those walls...", song.getLyrics());
    }

    @Test
    void testSetLyrics() {
        song.setLyrics("Hello, it's me...");
        assertEquals("Hello, it's me...", song.getLyrics());
    }

    @Test
    void testGetMusicalNotes() {
        assertEquals("C G Am F", song.getMusicalNotes());
    }

    @Test
    void testSetMusicalNotes() {
        song.setMusicalNotes("F#m D A E");
        assertEquals("F#m D A E", song.getMusicalNotes());
    }

    @Test
    void testGetGenre() {
        assertEquals("Pop", song.getGenre());
        assertEquals("Hip Hop", explicitSong.getGenre());
    }

    @Test
    void testSetGenre() {
        song.setGenre("Rock");
        assertEquals("Rock", song.getGenre());
    }

    @Test
    void testGetDurationInSeconds() {
        assertEquals(240, song.getDurationInSeconds());
        assertEquals(180, explicitSong.getDurationInSeconds());
    }

    @Test
    void testSetDurationInSeconds() {
        song.setDurationInSeconds(180);
        assertEquals(180, song.getDurationInSeconds());
        song.setDurationInSeconds(0);
        assertEquals(0, song.getDurationInSeconds());
    }

    @Test
    void testGetTimesPlayed() {
        assertEquals(0, song.getTimesPlayed());
        assertEquals(0, explicitSong.getTimesPlayed());
    }

    @Test
    void testSetTimesPlayed() {
        song.setTimesPlayed(5);
        assertEquals(5, song.getTimesPlayed());
        song.setTimesPlayed(42);
        assertEquals(42, song.getTimesPlayed());
    }

    @Test
    void testIncrementTimesPlayed() {
        assertEquals(0, song.getTimesPlayed());
        song.incrementTimesPlayed();
        assertEquals(1, song.getTimesPlayed());
        song.incrementTimesPlayed();
        assertEquals(2, song.getTimesPlayed());
        song.setTimesPlayed(10);
        song.incrementTimesPlayed();
        assertEquals(11, song.getTimesPlayed());
    }

    @Test
    void testIsExplicit() {
        assertFalse(song.isExplicit());
        assertTrue(explicitSong.isExplicit());
        assertFalse(multimediaSong.isExplicit());
    }

    @Test
    void testIsMultimedia() {
        assertFalse(song.isMultimedia());
        assertFalse(explicitSong.isMultimedia());
        assertTrue(multimediaSong.isMultimedia());
    }

    @Test
    void testGetVideoLink() {
        assertEquals("https://example.com/video", ((MultimediaSong) multimediaSong).getVideoLink());
    }

    @Test
    void testEquals() {
        Song copy = new Song(song);
        assertEquals(song, copy);
        assertEquals(song, song);
        assertNotEquals(song, null);
        assertNotEquals(song, "notASong");

        Song diffName = new Song("Different", "Beyonce", "Columbia", "Remember those walls...", "C G Am F", "Pop", 240);
        assertNotEquals(song, diffName);

        Song diffArtist = new Song("Halo", "Different", "Columbia", "Remember those walls...", "C G Am F", "Pop", 240);
        assertNotEquals(song, diffArtist);

        Song diffDuration = new Song("Halo", "Beyonce", "Columbia", "Remember those walls...", "C G Am F", "Pop", 300);
        assertNotEquals(song, diffDuration);

        Song other = new Song(song);
        other.setTimesPlayed(99);
        assertNotEquals(song, other);
    }

    @Test
    void testHashCode() {
        Song copy = new Song(song);
        assertEquals(song.hashCode(), copy.hashCode());

        Song diff = new Song("Other", "Other", "Other", "", "", "Other", 120);
        assertNotEquals(song.hashCode(), diff.hashCode());

        Song sameKey = new Song("Halo", "Beyonce", "Different", "Different", "Different", "Different", 240);
        assertEquals(song.hashCode(), sameKey.hashCode());
    }

    @Test
    void testClone() {
        Song cloned = song.clone();
        assertEquals(song, cloned);
        assertNotSame(song, cloned);

        Song clonedExplicit = explicitSong.clone();
        assertEquals(explicitSong, clonedExplicit);
        assertTrue(clonedExplicit.isExplicit());

        Song clonedMultimedia = multimediaSong.clone();
        assertEquals(multimediaSong, clonedMultimedia);
        assertTrue(clonedMultimedia.isMultimedia());
        assertEquals(((MultimediaSong) multimediaSong).getVideoLink(), ((MultimediaSong) clonedMultimedia).getVideoLink());
    }

    @Test
    void testToString() {
        String str = song.toString();
        assertTrue(str.contains("name='Halo'"));
        assertTrue(str.contains("artist='Beyonce'"));
        assertTrue(str.contains("publisher='Columbia'"));
        assertTrue(str.contains("durationInSeconds=240"));
        assertTrue(str.contains("timesPlayed=0"));
    }

    @Test
    void testCopyConstructor() {
        Song copied = new Song(song);
        assertEquals(song, copied);
        assertNotSame(song, copied);

        ExplicitSong copiedExplicit = new ExplicitSong((ExplicitSong) explicitSong);
        assertTrue(copiedExplicit.isExplicit());
        assertNotSame(explicitSong, copiedExplicit);

        MultimediaSong copiedMultimedia = new MultimediaSong(multimediaSong);
        assertTrue(copiedMultimedia.isMultimedia());
        assertEquals(((MultimediaSong) multimediaSong).getVideoLink(), copiedMultimedia.getVideoLink());
    }
}
