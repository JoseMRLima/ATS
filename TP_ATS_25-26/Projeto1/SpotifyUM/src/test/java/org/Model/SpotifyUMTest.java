package org.Model;

import org.Model.Music.Music;
import org.Model.Album.Album;
import org.Model.User.User;
import org.Model.Playlist.Playlist;
import org.Model.Playlist.PlaylistFavorites;
import org.Model.Playlist.PlaylistRandom;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Plan.PlanPremiumTop;
import org.Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifUMTest {

    private SpotifUM model;

    @BeforeEach
    void setUp() {
        model = new SpotifUM();
    }

    // --- helpers ---

    private void addAlbumAndMusic(String musicName, String albumName) throws AlreadyExistsException {
        if (!model.albumExists(albumName)) {
            model.addNewAlbum(albumName, "TestArtist");
        }
        if (!model.musicExists(musicName)) {
            model.addNewMusic(musicName, "TestArtist", "Pub", "Lyrics here", "Notes",
                    "Rock", albumName, 200, false, null);
        }
    }

    private void loginFreeUser(String name) {
        model.addNewUser(name, name + "@t.com", "addr", "pass");
        try { model.authenticateUser(name, "pass"); } catch (Exception e) { fail(e.getMessage()); }
    }

    private void loginPremiumUser(String name) {
        loginFreeUser(name);
        model.getCurrentUser().setPlan(new PlanPremiumBase());
    }

    @Test
    void testDefaultConstructorEmptyState() {
        assertFalse(model.userExists("anyone"));
        assertFalse(model.musicExists("anything"));
        assertFalse(model.albumExists("anything"));
        assertEquals(0, model.getPublicPlaylistSize());
    }

    @Test
    void testParametrizedConstructor() {
        Map<String, Music> m = new HashMap<>();
        Map<Integer, Playlist> p = new HashMap<>();
        Map<String, User> u = new HashMap<>();
        Map<String, Album> a = new HashMap<>();
        Map<String, Integer> art = new HashMap<>();
        Map<String, Integer> gen = new HashMap<>();
        SpotifUM s = new SpotifUM(m, p, u, a, art, gen);
        assertNotNull(s);
        assertFalse(s.userExists("x"));
    }

    @Test
    void testCopyConstructorAndEquals() {
        Map<String, Music> m = new HashMap<>();
        Map<Integer, Playlist> p = new HashMap<>();
        Map<String, User> u = new HashMap<>();
        Map<String, Album> a = new HashMap<>();
        SpotifUM base = new SpotifUM(m, p, u, a, new HashMap<>(), new HashMap<>());
        SpotifUM copy = new SpotifUM(base);
        assertEquals(base, copy);
        assertNotSame(base, copy);
    }

    @Test
    void testToString() {
        assertEquals("SpotifUM(...)", model.toString());
    }

    @Test
    void testEqualsWithCurrentUser() {
        model.addNewUser("u", "u@u.com", "a", "pw");
        try { model.authenticateUser("u", "pw"); } catch (Exception e) { fail(); }
        SpotifUM other = new SpotifUM(model.getMusics(), model.getPublicPlaylists(),
                model.getUsers(), model.getAlbums(),
                model.getArtistReproductions(), model.getGenreReproductions());
        assertNotEquals(model, other);
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(model, model);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(model, null);
    }

    @Test
    void testEqualsDifferentType() {
        assertNotEquals(model, "string");
    }

    @Test
    void testAddNewUserAndExists() {
        assertFalse(model.userExists("alice"));
        model.addNewUser("alice", "a@b.com", "addr", "pw");
        assertTrue(model.userExists("alice"));
    }

    @Test
    void testAuthenticateUserSuccess() throws Exception {
        model.addNewUser("bob", "b@c.com", "addr", "secret");
        model.authenticateUser("bob", "secret");
        assertEquals("bob", model.getCurrentUser().getUsername());
    }

    @Test
    void testAuthenticateUserNotFound() {
        assertThrows(NotFoundException.class, () -> model.authenticateUser("ghost", "pw"));
    }

    @Test
    void testAuthenticateUserWrongPassword() {
        model.addNewUser("bob", "b@c.com", "addr", "secret");
        assertThrows(UnsupportedOperationException.class,
                () -> model.authenticateUser("bob", "wrong"));
    }

    @Test
    void testIsPasswordCorrect() {
        loginFreeUser("u1");
        assertTrue(model.isPasswordCorrect("pass"));
        assertFalse(model.isPasswordCorrect("wrong"));
    }

    @Test
    void testSetCurrentUserEmail() {
        loginFreeUser("u1");
        model.setCurrentUserEmail("new@email.com");
        assertEquals("new@email.com", model.getCurrentUser().getEmail());
    }

    @Test
    void testSetCurrentUserPassword() {
        loginFreeUser("u1");
        model.setCurrentUserPassword("newpass");
        assertTrue(model.isPasswordCorrect("newpass"));
    }

    @Test
    void testSetCurrentUserUsername() {
        loginFreeUser("u1");
        model.setCurrentUserUsername("u1_renamed");
        assertEquals("u1_renamed", model.getCurrentUser().getUsername());
    }

    @Test
    void testChangeCurrentUserName() {
        loginFreeUser("u1");
        model.changeCurrentUserName("u1_new");
        assertEquals("u1_new", model.getCurrentUser().getUsername());
        assertTrue(model.userExists("u1_new"));
        assertFalse(model.userExists("u1"));
    }

    @Test
    void testChangeCurrentUserNameSameNameThrows() {
        loginFreeUser("u1");
        assertThrows(IllegalArgumentException.class, () -> model.changeCurrentUserName("u1"));
    }

    @Test
    void testChangeCurrentUserNameAlreadyExistsThrows() {
        model.addNewUser("u1", "a@a.com", "a", "pw");
        model.addNewUser("u2", "b@b.com", "b", "pw");
        try { model.authenticateUser("u1", "pw"); } catch (Exception e) { fail(); }
        assertThrows(IllegalArgumentException.class, () -> model.changeCurrentUserName("u2"));
    }

    @Test
    void testHasLibraryFreeUser() {
        loginFreeUser("u1");
        assertFalse(model.hasLibrary());
    }

    @Test
    void testHasLibraryPremiumUser() {
        loginPremiumUser("u1");
        assertTrue(model.hasLibrary());
    }

    @Test
    void testAddPointsToCurrentUser() {
        loginPremiumUser("u1");
        int before = model.getCurrentUser().getPlan().getPoints();
        model.addPointsToCurrentUser();
        assertTrue(model.getCurrentUser().getPlan().getPoints() >= before);
    }

    @Test
    void testGetCurrentUserPlanName_free() {
        loginFreeUser("u1");
        assertEquals("Free", model.getCurrentUserPlanName());
    }

    @Test
    void testGetCurrentUserPlanName_premium() {
        loginPremiumUser("u1");
        assertEquals("PremiumBase", model.getCurrentUserPlanName());
    }

    @Test
    void testMusicExistsAndAdd() throws AlreadyExistsException {
        model.addNewAlbum("AlbumA", "Artist");
        assertFalse(model.musicExists("Song1"));
        model.addNewMusic("Song1", "Artist", "Pub", "Lyrics", "Notes", "Rock", "AlbumA", 180, false, null);
        assertTrue(model.musicExists("Song1"));
    }

    @Test
    void testAddNewMusicDuplicateThrows() throws AlreadyExistsException {
        model.addNewAlbum("AlbumA", "Artist");
        model.addNewMusic("Song1", "Artist", "Pub", "Lyrics", "Notes", "Rock", "AlbumA", 180, false, null);
        assertThrows(AlreadyExistsException.class,
                () -> model.addNewMusic("Song1", "Artist", "Pub", "Lyrics", "Notes", "Rock", "AlbumA", 180, false, null));
    }

    @Test
    void testAddNewMusicWithUrl() throws AlreadyExistsException {
        model.addNewAlbum("AlbumA", "Artist");
        model.addNewMusic("VideoSong", "Artist", "Pub", "Lyrics", "Notes", "Pop", "AlbumA", 200, false, "http://video.url");
        assertTrue(model.musicExists("VideoSong"));
    }

    @Test
    void testGetMusicByName() throws Exception {
        addAlbumAndMusic("SongX", "AlbX");
        Music m = model.getMusicByName("SongX");
        assertEquals("SongX", m.getName());
    }

    @Test
    void testGetMusicByNameNotFound() {
        assertThrows(NotFoundException.class, () -> model.getMusicByName("NoSong"));
    }

    @Test
    void testPlayMusic() throws Exception {
        addAlbumAndMusic("PlaySong", "AlbP");
        String lyrics = model.playMusic("PlaySong");
        assertEquals("Lyrics here", lyrics);
    }

    @Test
    void testPlayMusicNotFound() {
        assertThrows(NotFoundException.class, () -> model.playMusic("Ghost"));
    }

    @Test
    void testListAllMusics() throws AlreadyExistsException {
        model.addNewAlbum("AlbumA", "Artist");
        model.addNewMusic("Alpha", "Artist", "Pub", "Lyrics", "Notes", "Rock", "AlbumA", 200, false, null);
        String result = model.listAllMusics();
        assertTrue(result.contains("Alpha"));
        assertTrue(result.contains("Artist"));
    }

    @Test
    void testAddToCurrentUserMusicReproductions() throws AlreadyExistsException {
        loginFreeUser("u1");
        addAlbumAndMusic("SongR", "AlbR");
        model.addToCurrentUserMusicReproductions("SongR");
        assertEquals(1, model.getCurrentUser().getMusicReproductions().size());
    }

    @Test
    void testIncrementGenreReproductions() {
        model.incrementGenreReproductions("Rock");
        model.incrementGenreReproductions("Rock");
        model.incrementGenreReproductions("Pop");
        assertEquals(2, model.getGenreReproductions().get("Rock"));
        assertEquals(1, model.getGenreReproductions().get("Pop"));
    }

    @Test
    void testIncrementArtistReproductions() {
        model.incrementArtistReproductions("Queen");
        model.incrementArtistReproductions("Queen");
        assertEquals(2, model.getArtistReproductions().get("Queen"));
    }

    @Test
    void testAlbumCRUD() throws Exception {
        assertFalse(model.albumExists("MyAlbum"));
        model.addNewAlbum("MyAlbum", "me");
        assertTrue(model.albumExists("MyAlbum"));
        Album a = model.getAlbumByName("MyAlbum");
        assertEquals("MyAlbum", a.getName());
    }

    @Test
    void testGetAlbumByNameNotFound() {
        assertThrows(NotFoundException.class, () -> model.getAlbumByName("xxx"));
    }

    @Test
    void testAddMusicToAlbum() throws Exception {
        model.addNewAlbum("Alb1", "Art");
        Music m = new Music("M1", "Art", "Pub", "Lyr", "Notes", "Rock", "Alb1", 100, false);
        model.addMusicToAlbum("Alb1", m);
        Album a = model.getAlbumByName("Alb1");
        assertEquals("M1", a.getMusicByName("M1").getName());
    }

    @Test
    void testAddMusicToAlbumNotFound() {
        Music m = new Music("M1", "Art", "Pub", "Lyr", "Notes", "Rock", "X", 100, false);
        assertThrows(NotFoundException.class, () -> model.addMusicToAlbum("NoAlbum", m));
    }

    @Test
    void testListAllAlbums() {
        model.addNewAlbum("AlbTest", "ArtTest");
        String result = model.listAllAlbums();
        assertTrue(result.contains("AlbTest"));
        assertTrue(result.contains("ArtTest"));
    }

    @Test
    void testAddPublicPlaylist() {
        assertEquals(0, model.getPublicPlaylistSize());
        model.addPlaylist("TopHits", "admin");
        assertEquals(1, model.getPublicPlaylistSize());
    }

    @Test
    void testListPublicPlaylists() {
        model.addPlaylist("BestOf", "admin");
        String result = model.listPublicPlaylists();
        assertTrue(result.contains("BestOf"));
    }

    @Test
    void testAddToCurrentUserPlaylist() {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        assertEquals(1, model.getCurrentUser().getUserPlaylistCount());
    }

    @Test
    void testAddMusicToCurrentUserPlaylist() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        addAlbumAndMusic("SongAdd", "AlbAdd");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.addMusicToCurrentUserPlaylist(id, "SongAdd");
        assertEquals(1, model.getUserPlaylistById(id).getMusics().size());
    }

    @Test
    void testAddMusicToCurrentUserPlaylistMusicNotFound() {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        assertThrows(NotFoundException.class,
                () -> model.addMusicToCurrentUserPlaylist(id, "NoSong"));
    }

    @Test
    void testRemoveMusicFromPlaylist() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        addAlbumAndMusic("SongRem", "AlbRem");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.addMusicToCurrentUserPlaylist(id, "SongRem");
        model.removeMusicFromPlaylist("SongRem", id);
        assertEquals(0, model.getUserPlaylistById(id).getMusics().size());
    }

    @Test
    void testRemoveMusicFromPlaylistMusicNotInSystem() {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        assertThrows(NotFoundException.class,
                () -> model.removeMusicFromPlaylist("Nonexistent", id));
    }

    @Test
    void testSetPlaylistAsPublic() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.setPlaylistAsPublic(id);
        assertEquals(1, model.getPublicPlaylistSize());
    }

    @Test
    void testSetPlaylistAsPublicAlreadyPublicThrows() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.setPlaylistAsPublic(id);
        assertThrows(AlreadyExistsException.class, () -> model.setPlaylistAsPublic(id));
    }

    @Test
    void testGetPublicPlaylistById() {
        model.addPlaylist("Public1", "admin");
        Playlist p = model.getPublicPlaylists().values().iterator().next();
        Playlist result = model.getPublicPlaylistById(p.getId());
        assertEquals("Public1", result.getName());
    }

    @Test
    void testAddPlaylistToCurrentUserLibrary() throws Exception {
        loginPremiumUser("u1");
        model.addPlaylist("PublicList", "admin");
        Playlist pub = model.getPublicPlaylists().values().iterator().next();
        model.addPlaylistToCurrentUserLibrary(pub.getId());
        assertEquals(1, model.getCurrentUser().getPlaylists().size());
    }

    @Test
    void testAddPlaylistToCurrentUserLibraryNotFound() {
        loginPremiumUser("u1");
        assertThrows(NotFoundException.class, () -> model.addPlaylistToCurrentUserLibrary(9999));
    }

    @Test
    void testAddPlaylistToCurrentUserLibraryDuplicate() throws Exception {
        loginPremiumUser("u1");
        model.addPlaylist("PublicList", "admin");
        Playlist pub = model.getPublicPlaylists().values().iterator().next();
        model.addPlaylistToCurrentUserLibrary(pub.getId());
        assertThrows(AlreadyExistsException.class,
                () -> model.addPlaylistToCurrentUserLibrary(pub.getId()));
    }

    @Test
    void testListCurrentUserPlaylists() {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("Favorites");
        String result = model.listCurrentUserPlaylists();
        assertTrue(result.contains("Favorites"));
    }

    @Test
    void testListAllMusicsInPlaylist() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyList");
        addAlbumAndMusic("SongList", "AlbList");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.addMusicToCurrentUserPlaylist(id, "SongList");
        String result = model.listAllMusicsInPlaylist(id);
        assertTrue(result.contains("SongList"));
    }

    @Test
    void testCreateGenrePlaylist() throws Exception {
        loginPremiumUser("u1");
        addAlbumAndMusic("RockSong1", "RockAlb");
        addAlbumAndMusic("RockSong2", "RockAlb");
        model.createGenrePlaylist("MyRock", "Rock", 9999);
        assertEquals(1, model.getCurrentUser().getUserPlaylistCount());
    }

    @Test
    void testCreateGenrePlaylistNoMusicsThrows() {
        loginPremiumUser("u1");
        assertThrows(IllegalArgumentException.class,
                () -> model.createGenrePlaylist("Jazz", "Jazz", 9999));
    }

    @Test
    void testFavoritesPlaylist() {
        loginFreeUser("u1");
        PlaylistFavorites fav = model.createFavoritesPlaylist(1000, false);
        assertNotNull(fav);
        assertTrue(fav.getMusics().isEmpty());
    }

    @Test
    void testRandomPlaylistEmpty() {
        PlaylistRandom rnd = model.getRandomPlaylist();
        assertNotNull(rnd);
        assertTrue(rnd.getMusics().isEmpty());
    }

    @Test
    void testRandomPlaylistWithMusics() throws AlreadyExistsException {
        addAlbumAndMusic("R1", "AlbR");
        addAlbumAndMusic("R2", "AlbR");
        PlaylistRandom rnd = model.getRandomPlaylist();
        assertFalse(rnd.getMusics().isEmpty());
    }

    @Test
    void testListAllGenres() throws AlreadyExistsException {
        model.addNewAlbum("A1", "Art");
        model.addNewMusic("S1", "Art", "P", "L", "N", "Rock", "A1", 100, false, null);
        model.addNewMusic("S2", "Art", "P", "L", "N", "Pop", "A1", 100, false, null);
        String result = model.listAllGenres();
        assertTrue(result.contains("Rock"));
        assertTrue(result.contains("Pop"));
    }

    @Test
    void testMostReproducedMusic() throws Exception {
        addAlbumAndMusic("HitSong", "HitAlb");
        model.playMusic("HitSong");
        model.playMusic("HitSong");
        Music top = model.mostReproducedMusic();
        assertEquals("HitSong", top.getName());
    }

    @Test
    void testMostReproducedMusicEmptyThrows() {
        assertThrows(NoMusicsInDatabaseException.class, () -> model.mostReproducedMusic());
    }

    @Test
    void testGetTopArtistName() {
        model.incrementArtistReproductions("Queen");
        model.incrementArtistReproductions("Queen");
        model.incrementArtistReproductions("Beatles");
        assertDoesNotThrow(() -> {
            String result = model.getTopArtistName();
            assertTrue(result.contains("Queen"));
        });
    }

    @Test
    void testGetTopArtistNameEmptyThrows() {
        assertThrows(NoArtistsInDatabaseException.class, () -> model.getTopArtistName());
    }

    @Test
    void testGetUserWithMostPoints() throws Exception {
        model.addNewUser("u1", "a@a.com", "a", "pw");
        model.addNewUser("u2", "b@b.com", "b", "pw");
        model.authenticateUser("u1", "pw");
        model.getCurrentUser().setPlan(new PlanPremiumBase());
        model.getCurrentUser().getPlan().setPoints(100);
        User top = model.getUserWithMostPoints();
        assertNotNull(top);
    }

    @Test
    void testGetUserWithMostPointsEmptyThrows() {
        assertThrows(NoUsersInDatabaseException.class, () -> model.getUserWithMostPoints());
    }

    @Test
    void testGetGenreWithMostReproductions() throws Exception {
        model.incrementGenreReproductions("Rock");
        model.incrementGenreReproductions("Rock");
        model.incrementGenreReproductions("Pop");
        String result = model.getGenreWithMostReproductions();
        assertEquals("Rock", result);
    }

    @Test
    void testGetGenreWithMostReproductionsEmptyThrows() {
        assertThrows(NoReproductionsInDatabaseException.class,
                () -> model.getGenreWithMostReproductions());
    }

    @Test
    void testGetUserWithMostPlaylists() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("P1");
        model.addToCurrentUserPlaylist("P2");
        User top = model.getUserWithMostPlaylists();
        assertEquals("u1", top.getUsername());
    }

    @Test
    void testGetUserWithMostPlaylistsEmptyThrows() {
        assertThrows(NoUsersInDatabaseException.class, () -> model.getUserWithMostPlaylists());
    }

    @Test
    void testGetUserWithMostReproductionsNoDate() throws Exception {
        loginFreeUser("u1");
        addAlbumAndMusic("SongRep", "AlbRep");
        model.addToCurrentUserMusicReproductions("SongRep");
        User top = model.getUserWithMostReproductions();
        assertEquals("u1", top.getUsername());
    }

    @Test
    void testGetUserWithMostReproductionsNoDateEmptyThrows() {
        assertThrows(NoUsersInDatabaseException.class, () -> model.getUserWithMostReproductions());
    }

    @Test
    void testGetUserWithMostReproductionsDateRange() throws Exception {
        loginFreeUser("u1");
        addAlbumAndMusic("SongDate", "AlbDate");
        model.addToCurrentUserMusicReproductions("SongDate");
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        User top = model.getUserWithMostReproductions(start, end);
        assertEquals("u1", top.getUsername());
    }

    @Test
    void testGetUserWithMostReproductionsDateRangeNoReproductions() {
        model.addNewUser("u1", "a@a.com", "a", "pw");
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        assertThrows(NoUsersInDatabaseException.class,
                () -> model.getUserWithMostReproductions(start, end));
    }

    @Test
    void testGetUserWithMostReproductionsEmptyThrows() {
        assertThrows(NoUsersInDatabaseException.class,
                () -> model.getUserWithMostReproductions(LocalDate.now(), LocalDate.now()));
    }

    @Test
    void testCanCurrentUserSkip_free() {
        loginFreeUser("u1");
        assertFalse(model.canCurrentUserSkip());
    }

    @Test
    void testCanCurrentUserSkip_premium() {
        loginPremiumUser("u1");
        assertTrue(model.canCurrentUserSkip());
    }

    @Test
    void testCanCurrentUserChooseWhatToPlay_free() {
        loginFreeUser("u1");
        assertFalse(model.canCurrentUserChooseWhatToPlay());
    }

    @Test
    void testCanCurrentUserChooseWhatToPlay_premium() {
        loginPremiumUser("u1");
        assertTrue(model.canCurrentUserChooseWhatToPlay());
    }

    @Test
    void testCurrentUserAccessToFavorites_free() {
        loginFreeUser("u1");
        assertFalse(model.currentUserAccessToFavorites());
    }

    @Test
    void testCurrentUserAccessToFavorites_premiumTop() {
        loginFreeUser("u1");
        model.getCurrentUser().setPlan(new PlanPremiumTop());
        assertTrue(model.currentUserAccessToFavorites());
    }

    @Test
    void testGettersAndSetters() {
        Map<String, Music> m = new HashMap<>();
        Map<Integer, Playlist> p = new HashMap<>();
        Map<String, User> u = new HashMap<>();
        Map<String, Album> a = new HashMap<>();
        Map<String, Integer> art = new HashMap<>();
        Map<String, Integer> gen = new HashMap<>();

        model.setMusics(m);
        model.setPublicPlaylists(p);
        model.setUsers(u);
        model.setAlbums(a);
        model.setArtistReproductions(art);
        model.setGenreReproductions(gen);

        assertNotNull(model.getMusics());
        assertNotNull(model.getPublicPlaylists());
        assertNotNull(model.getUsers());
        assertNotNull(model.getAlbums());
        assertNotNull(model.getArtistReproductions());
        assertNotNull(model.getGenreReproductions());
    }

    @Test
    void testSetAndGetCurrentUser() {
        User u = new User("testU", "t@t.com", "addr", "pw");
        model.setCurrentUser(u);
        assertEquals("testU", model.getCurrentUser().getUsername());
    }

    @Test
    void testParametrizedConstructorCopiesMusics() throws AlreadyExistsException {
        model.addNewAlbum("Alb", "Art");
        model.addNewMusic("S1", "Art", "Pub", "Lyrics", "Notes", "Rock", "Alb", 200, false, null);
        Map<String, Music> m = model.getMusics();
        Map<Integer, Playlist> p = model.getPublicPlaylists();
        Map<String, User> u = model.getUsers();
        Map<String, Album> a = model.getAlbums();
        Map<String, Integer> art = new HashMap<>();
        art.put("Art", 5);
        Map<String, Integer> gen = new HashMap<>();
        gen.put("Rock", 3);

        SpotifUM s = new SpotifUM(m, p, u, a, art, gen);

        assertTrue(s.musicExists("S1"));
        assertEquals(1, s.getMusics().size());
        assertEquals(5, s.getArtistReproductions().get("Art"));
        assertEquals(3, s.getGenreReproductions().get("Rock"));
    }

    @Test
    void testCopyConstructorCopiesAllData() throws AlreadyExistsException {
        model.addNewAlbum("Alb", "Art");
        model.addNewMusic("Song", "Art", "Pub", "Lyrics", "Notes", "Rock", "Alb", 200, false, null);
        model.addNewUser("usr", "u@u.com", "addr", "pw");
        model.addPlaylist("PubPl", "admin");
        model.incrementArtistReproductions("Art");
        model.incrementGenreReproductions("Rock");

        SpotifUM copy = new SpotifUM(model);

        assertTrue(copy.musicExists("Song"));
        assertTrue(copy.userExists("usr"));
        assertTrue(copy.albumExists("Alb"));
        assertEquals(1, copy.getPublicPlaylistSize());
        assertEquals(1, copy.getArtistReproductions().get("Art"));
        assertEquals(1, copy.getGenreReproductions().get("Rock"));
    }

    @Test
    void testCopyConstructorWithCurrentUserIsCopied() {
        loginFreeUser("u1");
        SpotifUM copy = new SpotifUM(model);
        assertNotNull(copy.getCurrentUser());
        assertEquals("u1", copy.getCurrentUser().getUsername());
    }

    @Test
    void testGetMusicsReturnsContent() throws AlreadyExistsException {
        model.addNewAlbum("A", "Art");
        model.addNewMusic("M", "Art", "Pub", "L", "N", "Rock", "A", 100, false, null);
        Map<String, Music> result = model.getMusics();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("M"));
    }

    @Test
    void testGetUsersReturnsContent() {
        model.addNewUser("alice", "a@b.com", "addr", "pw");
        Map<String, User> result = model.getUsers();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("alice"));
    }

    @Test
    void testGetAlbumsReturnsContent() {
        model.addNewAlbum("AlbX", "Art");
        Map<String, Album> result = model.getAlbums();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("AlbX"));
    }

    @Test
    void testGetPublicPlaylistsReturnsContent() {
        model.addPlaylist("PL1", "admin");
        Map<Integer, Playlist> result = model.getPublicPlaylists();
        assertEquals(1, result.size());
    }

    @Test
    void testGetArtistReproductionsReturnsContent() {
        model.incrementArtistReproductions("Queen");
        model.incrementArtistReproductions("Queen");
        Map<String, Integer> result = model.getArtistReproductions();
        assertEquals(1, result.size());
        assertEquals(2, result.get("Queen"));
    }

    @Test
    void testGetGenreReproductionsReturnsContent() {
        model.incrementGenreReproductions("Jazz");
        Map<String, Integer> result = model.getGenreReproductions();
        assertEquals(1, result.size());
        assertEquals(1, result.get("Jazz"));
    }

    @Test
    void testEqualsDifferentMusics() throws AlreadyExistsException {
        SpotifUM other = new SpotifUM();
        model.addNewAlbum("A", "Art");
        model.addNewMusic("OnlyHere", "Art", "P", "L", "N", "Rock", "A", 100, false, null);
        assertNotEquals(model, other);
    }

    @Test
    void testEqualsDifferentUsers() {
        SpotifUM other = new SpotifUM();
        model.addNewUser("onlyHere", "o@o.com", "a", "pw");
        assertNotEquals(model, other);
    }

    @Test
    void testEqualsDifferentAlbums() {
        SpotifUM other = new SpotifUM();
        model.addNewAlbum("OnlyAlbum", "Art");
        assertNotEquals(model, other);
    }

    @Test
    void testEqualsDifferentPublicPlaylists() {
        SpotifUM other = new SpotifUM();
        model.addPlaylist("OnlyPL", "admin");
        assertNotEquals(model, other);
    }

    @Test
    void testEqualsTwoEmptyModels() {
        assertEquals(new SpotifUM(), new SpotifUM());
    }

    @Test
    void testGetUserWithMostPointsReturnsCorrectUser() throws Exception {
        model.addNewUser("rich", "r@r.com", "a", "pw");
        model.addNewUser("poor", "p@p.com", "b", "pw");
        model.authenticateUser("rich", "pw");
        model.getCurrentUser().setPlan(new PlanPremiumBase());
        model.getCurrentUser().getPlan().setPoints(999);
        User top = model.getUserWithMostPoints();
        assertEquals("rich", top.getUsername());
        assertEquals(999, top.getPlan().getPoints());
    }

    @Test
    void testGetTopArtistNameOrderedByMostReproduced() throws Exception {
        model.incrementArtistReproductions("A");
        model.incrementArtistReproductions("B");
        model.incrementArtistReproductions("B");
        model.incrementArtistReproductions("B");
        String result = model.getTopArtistName();
        int posA = result.indexOf("A");
        int posB = result.indexOf("B");
        assertTrue(posB < posA, "B should appear before A (more reproductions)");
    }

    @Test
    void testAddPointsToCurrentUserFreeUserGets5() {
        loginFreeUser("u1");
        int before = model.getCurrentUser().getPlan().getPoints();
        model.addPointsToCurrentUser();
        assertEquals(before + 5, model.getCurrentUser().getPlan().getPoints());
    }

    @Test
    void testAddPointsToCurrentUserPremiumBaseGets10() {
        loginPremiumUser("u1");
        int before = model.getCurrentUser().getPlan().getPoints();
        model.addPointsToCurrentUser();
        assertEquals(before + 10, model.getCurrentUser().getPlan().getPoints());
    }

    @Test
    void testMostReproducedMusicWithTwoSongs() throws Exception {
        model.addNewAlbum("A", "Art");
        model.addNewMusic("Hit", "Art", "P", "L", "N", "Rock", "A", 100, false, null);
        model.addNewMusic("Flop", "Art", "P", "L", "N", "Pop", "A", 100, false, null);
        model.playMusic("Hit");
        model.playMusic("Hit");
        model.playMusic("Hit");
        model.playMusic("Flop");
        Music top = model.mostReproducedMusic();
        assertEquals("Hit", top.getName());
    }

    @Test
    void testGetGenreWithMostReproductionsTwoGenres() throws Exception {
        model.incrementGenreReproductions("Classical");
        model.incrementGenreReproductions("Jazz");
        model.incrementGenreReproductions("Jazz");
        assertEquals("Jazz", model.getGenreWithMostReproductions());
    }

    @Test
    void testGetUserWithMostPlaylistsExact() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("P1");
        model.addToCurrentUserPlaylist("P2");
        model.addToCurrentUserPlaylist("P3");

        model.addNewUser("u2", "u2@u.com", "a", "pw");

        User top = model.getUserWithMostPlaylists();
        assertEquals("u1", top.getUsername());
        assertEquals(3, top.getUserPlaylistCount());
    }

    @Test
    void testGetUserWithMostReproductionsDateRangeOutOfRange() throws Exception {
        loginFreeUser("u1");
        addAlbumAndMusic("SongOld", "AlbOld");
        model.addToCurrentUserMusicReproductions("SongOld");
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);
        assertThrows(NoUsersInDatabaseException.class,
                () -> model.getUserWithMostReproductions(start, end));
    }

    @Test
    void testPopulateDatabase() {
        model.populateDatabase();
        assertTrue(model.musicExists("Bohemian Rhapsody"));
        assertTrue(model.musicExists("Shape of You"));
        assertTrue(model.musicExists("Never Gonna Give You Up"));
        assertTrue(model.albumExists("Divide"));
        assertTrue(model.albumExists("A Night at the Opera"));
        assertTrue(model.userExists("simao"));
        assertTrue(model.userExists("gabriel"));
        assertTrue(model.userExists("jose"));
        assertTrue(model.getPublicPlaylistSize() > 0);
    }

    @Test
    void testListAllMusicsInPlaylistNotFound() throws Exception {
        loginPremiumUser("u1");
        String result = model.listAllMusicsInPlaylist(99999);
        assertNotNull(result);
    }

    @Test
    void testGetUserPlaylistByIdNotFound() {
        loginPremiumUser("u1");
        assertThrows(RuntimeException.class, () -> model.getUserPlaylistById(99999));
    }

    @Test
    void testCreateFavoritesPlaylistExplicit() throws AlreadyExistsException {
        loginPremiumUser("u1");
        model.addNewAlbum("ExplAlb", "Art");
        model.addNewMusic("ExplSong", "Art", "P", "L", "N", "Rock", "ExplAlb", 100, true, null);
        model.addToCurrentUserMusicReproductions("ExplSong");
        PlaylistFavorites pf = model.createFavoritesPlaylist(9999, true);
        assertNotNull(pf);
        assertFalse(pf.getMusics().isEmpty());
    }

    @Test
    void testCreateFavoritesPlaylistNoFavorites() {
        loginPremiumUser("u1");
        PlaylistFavorites pf = model.createFavoritesPlaylist(9999, false);
        assertNotNull(pf);
        assertTrue(pf.getMusics().isEmpty());
    }

    @Test
    void testEqualsCurrentUserNullOtherNotNull() {
        SpotifUM other = new SpotifUM();
        model.addNewUser("u", "u@u.com", "a", "pw");
        try { model.authenticateUser("u", "pw"); } catch (Exception e) { fail(); }
        assertNotEquals(other, model);
    }

    @Test
    void testListAllMusicsEmptyReturnsEmpty() {
        String result = model.listAllMusics();
        assertEquals("", result);
    }

    @Test
    void testListAllAlbumsEmpty() {
        String result = model.listAllAlbums();
        assertEquals("", result);
    }

    @Test
    void testListPublicPlaylistsEmpty() {
        String result = model.listPublicPlaylists();
        assertEquals("", result);
    }

    @Test
    void testListAllGenresEmpty() {
        String result = model.listAllGenres();
        assertEquals("", result);
    }

    @Test
    void testListCurrentUserPlaylistsFreeUserThrows() {
        loginFreeUser("u1");
        assertThrows(UnsupportedOperationException.class, () -> model.listCurrentUserPlaylists());
    }

    @Test
    void testSetPlaylistAsPublicNullHandling() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("PublicMe");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.setPlaylistAsPublic(id);
        assertEquals(1, model.getPublicPlaylistSize());
    }

    @Test
    void testPopulateDatabaseAndPlayMusic() throws Exception {
        model.populateDatabase();
        String lyrics = model.playMusic("Bohemian Rhapsody");
        assertNotNull(lyrics);
        assertTrue(lyrics.contains("real life"));
    }

    @Test
    void testParametrizedConstructorWithNonEmptyPlaylists() {
        Map<String, Music> m = new HashMap<>();
        Map<Integer, Playlist> p = new HashMap<>();
        Playlist pl = new Playlist("P1", "admin", new java.util.ArrayList<>());
        p.put(pl.getId(), pl);
        Map<String, User> u = new HashMap<>();
        Map<String, Album> a = new HashMap<>();
        SpotifUM s = new SpotifUM(m, p, u, a, new HashMap<>(), new HashMap<>());
        assertEquals(1, s.getPublicPlaylistSize());
    }

    @Test
    void testSetPlaylistAsPublicNotFoundThrows() {
        loginPremiumUser("u1");
        assertThrows(NotFoundException.class,
                () -> model.setPlaylistAsPublic(999999));
    }

    @Test
    void testGetUserWithMostReproductionsTwoUsersNoDate() throws Exception {
        loginFreeUser("u1");
        addAlbumAndMusic("SongA1", "AlbA1");
        model.addToCurrentUserMusicReproductions("SongA1");
        model.addToCurrentUserMusicReproductions("SongA1");
        model.addNewUser("u2", "u2@t.com", "addr", "pw");
        try { model.authenticateUser("u2", "pw"); } catch (Exception e) { fail(e.getMessage()); }
        model.addToCurrentUserMusicReproductions("SongA1");
        User top = model.getUserWithMostReproductions();
        assertEquals("u1", top.getUsername());
    }

    @Test
    void testGetUserWithMostReproductionsTwoUsersDateRange() throws Exception {
        loginFreeUser("u1");
        addAlbumAndMusic("SongB1", "AlbB1");
        model.addToCurrentUserMusicReproductions("SongB1");
        model.addToCurrentUserMusicReproductions("SongB1");
        model.addNewUser("u2b", "u2b@t.com", "addr", "pw");
        try { model.authenticateUser("u2b", "pw"); } catch (Exception e) { fail(e.getMessage()); }
        model.addToCurrentUserMusicReproductions("SongB1");
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        User top = model.getUserWithMostReproductions(start, end);
        assertEquals("u1", top.getUsername());
    }

    @Test
    void testParametrizedConstructorWithNonEmptyUsersAndAlbums() {
        Map<String, Music> m = new HashMap<>();
        Map<Integer, Playlist> p = new HashMap<>();
        Map<String, User> u = new HashMap<>();
        u.put("alice", new User("alice", "a@a.com", "addr", "pw"));
        Map<String, Album> a = new HashMap<>();
        a.put("AlbX", new org.Model.Album.Album("AlbX", "Art"));
        SpotifUM s = new SpotifUM(m, p, u, a, new HashMap<>(), new HashMap<>());
        assertTrue(s.userExists("alice"));
        assertTrue(s.albumExists("AlbX"));
    }

    @Test
    void testAddNewMusicAlsoAddsToAlbumAndIsNotMultimedia() throws Exception {
        model.addNewAlbum("AlbumZ", "ArtZ");
        model.addNewMusic("MusicZ", "ArtZ", "Pub", "Lyrics", "Notes", "Rock", "AlbumZ", 200, false, null);
        org.Model.Album.Album alb = model.getAlbums().get("AlbumZ");
        Music found = alb.getMusics().stream().filter(ms -> ms.getName().equals("MusicZ")).findFirst().orElse(null);
        assertNotNull(found);
        assertFalse(found instanceof org.Model.Music.MusicMultimedia);
    }

    @Test
    void testAddNewMusicMultimediaAlsoAddsToAlbumAndIsMultimedia() throws Exception {
        model.addNewAlbum("AlbumMM", "ArtMM");
        model.addNewMusic("MusicMM", "ArtMM", "Pub", "Lyrics", "Notes", "Pop", "AlbumMM", 180, false, "http://url");
        org.Model.Album.Album alb = model.getAlbums().get("AlbumMM");
        Music found = alb.getMusics().stream().filter(ms -> ms.getName().equals("MusicMM")).findFirst().orElse(null);
        assertNotNull(found);
        assertTrue(found instanceof org.Model.Music.MusicMultimedia);
    }

    @Test
    void testChangeCurrentUserNameUpdatesPlaylistAutor() throws Exception {
        loginPremiumUser("u1");
        model.addToCurrentUserPlaylist("MyPL");
        model.changeCurrentUserName("u1renamed");
        List<org.Model.Playlist.Playlist> playlists = model.getCurrentUser().getPlaylists();
        assertTrue(playlists.stream().allMatch(pl -> pl.getAutor().equals("u1renamed")));
    }

    @Test
    void testListAllMusicsInPlaylistReturnsContent() throws Exception {
        loginPremiumUser("u1");
        model.addNewAlbum("AlbList", "ArtList");
        model.addNewMusic("SongList", "ArtList", "Pub", "L", "N", "Rock", "AlbList", 100, false, null);
        model.addToCurrentUserPlaylist("MyPlaylist");
        int id = model.getCurrentUser().getPlaylists().get(0).getId();
        model.addMusicToCurrentUserPlaylist(id, "SongList");
        String result = model.listAllMusicsInPlaylist(id);
        assertTrue(result.contains("SongList"));
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetUserWithMostPlaylistsTwoUsers() throws Exception {
        model.addNewUser("u2pit", "u2pit@t.com", "addr", "pw");
        loginPremiumUser("u1pit");
        model.addToCurrentUserPlaylist("PL1");
        model.addToCurrentUserPlaylist("PL2");
        User top = model.getUserWithMostPlaylists();
        assertEquals("u1pit", top.getUsername());
    }

    @Test
    void testMostReproducedMusicTwoMusics() throws Exception {
        model.addNewAlbum("AlbRep", "ArtRep");
        model.addNewMusic("HitSong", "ArtRep", "P", "L", "N", "Rock", "AlbRep", 100, false, null);
        model.addNewMusic("FlopSong", "ArtRep", "P", "L", "N", "Rock", "AlbRep", 100, false, null);
        model.playMusic("HitSong");
        model.playMusic("HitSong");
        model.playMusic("FlopSong");
        Music top = model.mostReproducedMusic();
        assertEquals("HitSong", top.getName());
    }

    @Test
    void testSettersPreserveData() {
        Map<String, User> users = new HashMap<>();
        users.put("bob", new User("bob", "b@b.com", "addr", "pw"));
        model.setUsers(users);
        assertTrue(model.userExists("bob"));

        Map<String, org.Model.Album.Album> albums = new HashMap<>();
        albums.put("AlbSet", new org.Model.Album.Album("AlbSet", "Art"));
        model.setAlbums(albums);
        assertTrue(model.albumExists("AlbSet"));

        Map<String, Integer> artReprod = new HashMap<>();
        artReprod.put("ArtSet", 5);
        model.setArtistReproductions(artReprod);
        assertEquals(5, (int) model.getArtistReproductions().get("ArtSet"));

        Map<String, Integer> genreReprod = new HashMap<>();
        genreReprod.put("Rock", 3);
        model.setGenreReproductions(genreReprod);
        assertEquals(3, (int) model.getGenreReproductions().get("Rock"));

        Map<Integer, org.Model.Playlist.Playlist> playlists = new HashMap<>();
        org.Model.Playlist.Playlist pl = new org.Model.Playlist.Playlist("PSet", "admin");
        playlists.put(pl.getId(), pl);
        model.setPublicPlaylists(playlists);
        assertEquals(1, model.getPublicPlaylistSize());

        Map<String, Music> musics = new HashMap<>();
        Music ms = new Music("MSet", "Art", "P", "L", "N", "Rock", "Alb", 100, false);
        musics.put("MSet", ms);
        model.setMusics(musics);
        assertTrue(model.musicExists("MSet"));
    }
}
