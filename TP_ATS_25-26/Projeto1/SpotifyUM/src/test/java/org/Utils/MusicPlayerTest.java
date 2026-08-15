package org.Utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicPlayerTest {

    @Test
    void testPlayMusicNonExistentReturnsNull() {
        MusicPlayer player = new MusicPlayer();
        // No WAV resource exists for this name → should return null gracefully
        assertNull(player.playMusic("SongThatDoesNotExist"));
    }

    @Test
    void testPlayMusicEmptyNameReturnsNull() {
        MusicPlayer player = new MusicPlayer();
        assertNull(player.playMusic(""));
    }
}
