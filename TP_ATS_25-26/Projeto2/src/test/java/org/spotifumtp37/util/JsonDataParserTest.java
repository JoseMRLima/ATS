package org.spotifumtp37.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spotifumtp37.exceptions.AlreadyExistsException;
import org.spotifumtp37.model.SpotifUMData;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataParserTest {

    private final JsonDataParser parser = new JsonDataParser();

    @Test
    void singleFileRoundTrip(@TempDir Path tmp) throws IOException, AlreadyExistsException {
        SpotifUMData original = new SpotifUMData();
        User user = new User("Alice", "alice@e.com", "Addr", new PremiumBase(), "pw", 100, new ArrayList<>());
        original.addUser(user);

        File file = tmp.resolve("data.json").toFile();
        parser.toJsonData(original, file.getAbsolutePath());
        SpotifUMData loaded = parser.fromJsonData(file.getAbsolutePath());

        assertTrue(loaded.existsUser("Alice"));
    }

    @Test
    void singleFileRoundTrip_WithAlbum(@TempDir Path tmp) throws IOException, AlreadyExistsException {
        SpotifUMData original = new SpotifUMData();
        Song song = new Song("S1", "Art", "Pub", "L", "N", "Rock", 200);
        Album album = new Album("Album1", "Art", 2020, "Rock", Collections.singletonList(song));
        original.addAlbum(album);

        File file = tmp.resolve("data.json").toFile();
        parser.toJsonData(original, file.getAbsolutePath());
        SpotifUMData loaded = parser.fromJsonData(file.getAbsolutePath());

        assertTrue(loaded.existsAlbum("Album1"));
    }

    @Test
    void multipleFilesRoundTrip(@TempDir Path tmp) throws IOException, AlreadyExistsException {
        SpotifUMData original = new SpotifUMData();
        User user = new User("Bob", "bob@e.com", "Addr", new FreePlan(), "pw", 0, new ArrayList<>());
        original.addUser(user);
        Song song = new Song("S2", "Art", "Pub", "L", "N", "Pop", 180);
        Album album = new Album("Album2", "Art", 2021, "Pop", Collections.singletonList(song));
        original.addAlbum(album);

        String dir = tmp.toFile().getAbsolutePath();
        parser.toJsonData(original, dir);
        SpotifUMData loaded = parser.fromJsonData(dir);

        assertTrue(loaded.existsUser("Bob"));
        assertTrue(loaded.existsAlbum("Album2"));
    }

    @Test
    void multipleFilesRoundTrip_MissingFilesReturnEmptyData(@TempDir Path tmp) throws IOException {
        String dir = tmp.toFile().getAbsolutePath();
        SpotifUMData loaded = parser.fromJsonData(dir);
        assertNotNull(loaded);
        assertFalse(loaded.existsUser("anyone"));
    }
}
