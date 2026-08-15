package org.Controller.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicInfoTest {

    @Test
    void testMainConstructorAndGetters() {
        MusicInfo info = new MusicInfo("Bohemian Rhapsody", "Queen", "Is this the real life", "http://url", false);
        assertEquals("Bohemian Rhapsody", info.getMusicName());
        assertEquals("Queen", info.getArtistName());
        assertNotNull(info.getLyrics());
        assertTrue(info.getLyrics().length > 0);
        assertEquals("Is", info.getLyrics()[0]);
        assertFalse(info.isExplicit());
        assertEquals("http://url", info.getUrl());
        assertEquals("", info.getErrorMessage());
    }

    @Test
    void testMainConstructorExplicit() {
        MusicInfo info = new MusicInfo("Song", "Artist", "lyrics here", null, true);
        assertTrue(info.isExplicit());
        assertNull(info.getUrl());
    }

    @Test
    void testErrorConstructor() {
        MusicInfo info = new MusicInfo("Something went wrong");
        assertEquals("", info.getMusicName());
        assertEquals("", info.getArtistName());
        assertNull(info.getLyrics());
        assertFalse(info.isExplicit());
        assertEquals("", info.getUrl());
        assertEquals("Something went wrong", info.getErrorMessage());
    }

    @Test
    void testSetErrorMessage() {
        MusicInfo info = new MusicInfo("Song", "Artist", "lyrics", "url", false);
        info.setErrorMessage("new error");
        assertEquals("new error", info.getErrorMessage());
    }

    @Test
    void testLyricsSplitBySpace() {
        MusicInfo info = new MusicInfo("S", "A", "one two three", "url", false);
        String[] lyrics = info.getLyrics();
        assertEquals(3, lyrics.length);
        assertEquals("one", lyrics[0]);
        assertEquals("two", lyrics[1]);
        assertEquals("three", lyrics[2]);
    }
}
