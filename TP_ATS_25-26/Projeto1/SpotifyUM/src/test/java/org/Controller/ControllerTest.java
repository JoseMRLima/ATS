package org.Controller;

import org.Controller.dtos.MusicInfo;
import org.Exceptions.*;
import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Plan.PlanPremiumTop;
import org.Model.Playlist.Playlist;
import org.Model.SpotifUM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private Controller ctrl;

    @BeforeEach
    void setUp() {
        ctrl = new Controller();
    }


    private void loginFree(String name) {
        ctrl.addNewUser(name, name + "@t.com", "addr", "pass");
        ctrl.loginWithMessage(name, "pass");
    }

    private void loginPremium(String name) {
        loginFree(name);
        ctrl.getSpotifUM().getCurrentUser().setPlan(new PlanPremiumBase());
    }

    private void addAlbumAndMusic(String music, String album) {
        ctrl.createAlbum(album, "Art");
        ctrl.addMusic(music, "Art", "Pub", "Lyrics", "Notes", "Rock", album, 180, false, null);
    }


    @Test
    void testDefaultConstructor() {
        assertNotNull(ctrl.getSpotifUM());
    }

    @Test
    void testCopyConstructor() {
        ctrl.addNewUser("u", "u@u.com", "a", "p");
        Controller copy = new Controller(ctrl);
        assertNotNull(copy.getSpotifUM());
    }

    @Test
    void testSpotifUMConstructor() {
        SpotifUM model = new SpotifUM();
        Controller c = new Controller(model);
        assertNotNull(c.getSpotifUM());
    }

    @Test
    void testSetSpotifUM() {
        SpotifUM newModel = new SpotifUM();
        ctrl.setSpotifUM(newModel);
        assertNotNull(ctrl.getSpotifUM());
    }


    @Test
    void testLoginWithMessageSuccess() {
        ctrl.addNewUser("alice", "a@b.com", "addr", "pw");
        assertTrue(ctrl.loginWithMessage("alice", "pw"));
    }

    @Test
    void testLoginWithMessageFail() {
        assertFalse(ctrl.loginWithMessage("ghost", "pw"));
    }

    @Test
    void testLoginWrongPassword() {
        ctrl.addNewUser("bob", "b@c.com", "addr", "secret");
        assertFalse(ctrl.loginWithMessage("bob", "wrong"));
    }

    @Test
    void testAddNewUserSuccess() {
        String result = ctrl.addNewUser("user1", "u@u.com", "addr", "pw");
        assertTrue(result.contains("✅"));
    }

    @Test
    void testAddNewUserDuplicate() {
        ctrl.addNewUser("user1", "u@u.com", "addr", "pw");
        String result = ctrl.addNewUser("user1", "u2@u.com", "addr2", "pw2");
        assertTrue(result.contains("⚠️"));
    }


    @Test
    void testExportAndImportModel() throws Exception {
        ctrl.addNewUser("testUser", "t@t.com", "addr", "pw");
        java.io.File tmp = java.io.File.createTempFile("spotifyum_test_", ".ser");
        tmp.deleteOnExit();

        String exportMsg = ctrl.exportModel(tmp.getAbsolutePath());
        assertTrue(exportMsg.contains("✅"));

        String importMsg = ctrl.importModel(tmp.getAbsolutePath());
        assertTrue(importMsg.contains("✅"));
        assertTrue(ctrl.getSpotifUM().userExists("testUser"));
    }

    @Test
    void testExportModelError() {
        String msg = ctrl.exportModel("/nonexistent/path/file.ser");
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testImportModelError() {
        String msg = ctrl.importModel("/nonexistent/file.ser");
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testCreateAlbumSuccess() {
        String msg = ctrl.createAlbum("MyAlbum", "MyArtist");
        assertTrue(msg.contains("✅"));
        assertTrue(ctrl.albumExists("MyAlbum"));
    }

    @Test
    void testAlbumExistsFalse() {
        assertFalse(ctrl.albumExists("NoAlbum"));
    }

    @Test
    void testAddMusicSuccess() {
        ctrl.createAlbum("Alb", "Art");
        String msg = ctrl.addMusic("Song", "Art", "Pub", "Lyrics", "Notes", "Rock", "Alb", 200, false, null);
        assertTrue(msg.contains("✅"));
        assertTrue(ctrl.musicExists("Song"));
    }

    @Test
    void testAddMusicDuplicateError() {
        ctrl.createAlbum("Alb", "Art");
        ctrl.addMusic("Song", "Art", "Pub", "L", "N", "Rock", "Alb", 200, false, null);
        String msg = ctrl.addMusic("Song", "Art", "Pub", "L", "N", "Rock", "Alb", 200, false, null);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testAddMusicWithUrl() {
        ctrl.createAlbum("Alb", "Art");
        String msg = ctrl.addMusic("VideoSong", "Art", "Pub", "L", "N", "Pop", "Alb", 200, false, "http://v.url");
        assertTrue(msg.contains("✅"));
    }


    @Test
    void testCurrentUserHasLibraryNullUser() {
        assertFalse(ctrl.currentUserHasLibrary());
    }

    @Test
    void testCurrentUserHasLibraryFree() {
        loginFree("u1");
        assertFalse(ctrl.currentUserHasLibrary());
    }

    @Test
    void testCurrentUserHasLibraryPremium() {
        loginPremium("u1");
        assertTrue(ctrl.currentUserHasLibrary());
    }

    @Test
    void testChangeCurrentUserUserNameSuccess() {
        loginFree("u1");
        String msg = ctrl.changeCurrentUserUserName("u1_new");
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testChangeCurrentUserUserNameError() {
        loginFree("u1");
        ctrl.addNewUser("u2", "u2@u.com", "a", "pw");
        String msg = ctrl.changeCurrentUserUserName("u2");
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testChangeCurrentUserEmail() {
        loginFree("u1");
        String msg = ctrl.changeCurrentUserEmail("new@email.com");
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testIsPasswordCorrect() {
        loginFree("u1");
        assertTrue(ctrl.isPasswordCorrect("pass"));
        assertFalse(ctrl.isPasswordCorrect("wrong"));
    }

    @Test
    void testChangeCurrentUserPassword() {
        loginFree("u1");
        String msg = ctrl.changeCurrentUserPassword("newpw");
        assertTrue(msg.contains("✅"));
        assertTrue(ctrl.isPasswordCorrect("newpw"));
    }

    @Test
    void testGetCurrentUser() {
        loginFree("u1");
        String info = ctrl.getCurrentUser();
        assertTrue(info.contains("u1"));
    }

    @Test
    void testSetFreePlan() {
        loginFree("u1");
        String msg = ctrl.setFreePlan();
        assertTrue(msg.contains("✅"));
        assertEquals("Free", ctrl.getCurrentUserPlan());
    }

    @Test
    void testSetPremiumBasePlan() {
        loginFree("u1");
        String msg = ctrl.setPremiumBasePlan();
        assertFalse(msg.isEmpty());
        assertEquals("PremiumBase", ctrl.getCurrentUserPlan());
    }

    @Test
    void testSetPremiumTopPlan() {
        loginFree("u1");
        String msg = ctrl.setPremiumTopPlan();
        assertFalse(msg.isEmpty());
        assertEquals("PremiumTop", ctrl.getCurrentUserPlan());
    }


    @Test
    void testAddToCurrentUserPlaylistsSuccess() {
        loginPremium("u1");
        String msg = ctrl.addToCurrentUserPlaylists("MyList");
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testAddToCurrentUserPlaylistsError() {
        loginFree("u1");
        String msg = ctrl.addToCurrentUserPlaylists("MyList");
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testAddMusicToCurrentUserPlaylistSuccess() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("MyList");
        addAlbumAndMusic("Song1", "Alb1");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        String msg = ctrl.addMusicToCurrentUserPlaylist(id, "Song1");
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testAddMusicToCurrentUserPlaylistError() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("MyList");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        String msg = ctrl.addMusicToCurrentUserPlaylist(id, "NoSong");
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testListUserPlaylistsEmpty() {
        loginPremium("u1");
        String msg = ctrl.listUserPlaylists();
        assertTrue(msg.contains("📭"));
    }

    @Test
    void testListUserPlaylistsWithData() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("MyFavs");
        String msg = ctrl.listUserPlaylists();
        assertTrue(msg.contains("MyFavs"));
    }

    @Test
    void testListUserPlaylistsError() {
        loginFree("u1");
        String msg = ctrl.listUserPlaylists();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testListPublicPlaylistsEmpty() {
        String msg = ctrl.listPublicPlaylists();
        assertTrue(msg.contains("📭"));
    }

    @Test
    void testListPublicPlaylistsWithData() {
        ctrl.getSpotifUM().addPlaylist("TopHits", "admin");
        String msg = ctrl.listPublicPlaylists();
        assertTrue(msg.contains("TopHits"));
    }

    @Test
    void testListPlaylistMusicsEmpty() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("EmptyList");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        String msg = ctrl.listPlaylistMusics(id);
        assertEquals("", msg);
    }

    @Test
    void testListPlaylistMusicsWithData() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("MyList");
        addAlbumAndMusic("ListSong", "ListAlb");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        ctrl.addMusicToCurrentUserPlaylist(id, "ListSong");
        String msg = ctrl.listPlaylistMusics(id);
        assertTrue(msg.contains("ListSong"));
    }

    @Test
    void testListPlaylistMusicsError() {
        loginFree("u1");
        String msg = ctrl.listPlaylistMusics(99999);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testAddPublicPlaylistToLibrarySuccess() throws AlreadyExistsException {
        loginPremium("u1");
        ctrl.getSpotifUM().addPlaylist("PubList", "admin");
        Playlist pub = ctrl.getSpotifUM().getPublicPlaylists().values().iterator().next();
        String msg = ctrl.addPublicPlaylistToLibrary(pub.getId());
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testAddPublicPlaylistToLibraryError() {
        loginPremium("u1");
        String msg = ctrl.addPublicPlaylistToLibrary(99999);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testRemoveMusicFromPlaylistSuccess() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("P");
        addAlbumAndMusic("RemSong", "RemAlb");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        ctrl.addMusicToCurrentUserPlaylist(id, "RemSong");
        String msg = ctrl.removeMusicFromPlaylist("RemSong", id);
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testRemoveMusicFromPlaylistError() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("P");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        String msg = ctrl.removeMusicFromPlaylist("NoSong", id);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testSetPlaylistAsPublicSuccess() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("PubMe");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        String msg = ctrl.setPlaylistAsPublic(id);
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testSetPlaylistAsPublicError() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("PubMe");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        ctrl.setPlaylistAsPublic(id);
        String msg = ctrl.setPlaylistAsPublic(id);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetPlaylistMusicNamesFound() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("P");
        addAlbumAndMusic("PMSong", "PMAlb");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        ctrl.addMusicToCurrentUserPlaylist(id, "PMSong");
        Optional<List<String>> result = ctrl.getPlaylistMusicNames(id);
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("PMSong"));
    }

    @Test
    void testGetPlaylistMusicNamesNotFound() {
        loginFree("u1");
        Optional<List<String>> result = ctrl.getPlaylistMusicNames(99999);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetPlaylistIdFound() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("NamedList");
        int id = ctrl.getSpotifUM().getCurrentUser().getPlaylists().get(0).getId();
        String name = ctrl.getPlaylistId(id);
        assertEquals("NamedList", name);
    }

    @Test
    void testGetPlaylistIdNotFound() {
        loginFree("u1");
        String result = ctrl.getPlaylistId(99999);
        assertTrue(result.contains("❌"));
    }

    @Test
    void testGetAlbumMusicNamesFound() {
        ctrl.createAlbum("A1", "Art");
        ctrl.addMusic("AS1", "Art", "P", "L", "N", "Rock", "A1", 100, false, null);
        Optional<List<String>> result = ctrl.getAlbumMusicNames("A1");
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("AS1"));
    }

    @Test
    void testGetAlbumMusicNamesNotFound() {
        Optional<List<String>> result = ctrl.getAlbumMusicNames("NoAlbum");
        assertFalse(result.isPresent());
    }

    @Test
    void testGetFavoritePlaylistMusicNames() {
        loginFree("u1");
        Optional<List<String>> result = ctrl.getFavoritePlaylistMusicNames(false, 9999);
        assertTrue(result.isPresent());
    }

    @Test
    void testCreateGenrePlaylistSuccess() {
        loginPremium("u1");
        ctrl.createAlbum("JazzAlb", "Art");
        ctrl.addMusic("JS1", "Art", "P", "L", "N", "Jazz", "JazzAlb", 100, false, null);
        ctrl.addMusic("JS2", "Art", "P", "L", "N", "Jazz", "JazzAlb", 100, false, null);
        String msg = ctrl.createGenrePlaylist("JazzMix", "Jazz", 9999);
        assertTrue(msg.contains("✅"));
    }

    @Test
    void testCreateGenrePlaylistError() {
        loginPremium("u1");
        String msg = ctrl.createGenrePlaylist("Empty", "NonExistentGenre", 9999);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetRandomPlaylistMusicNames() {
        addAlbumAndMusic("RandSong", "RandAlb");
        Optional<List<String>> result = ctrl.getRandomPlaylistMusicNames();
        assertTrue(result.isPresent());
    }


    @Test
    void testPlayMusicSuccess() {
        loginFree("u1");
        addAlbumAndMusic("PlayMe", "PlayAlb");
        MusicInfo info = ctrl.playMusic("PlayMe");
        assertEquals("PlayMe", info.getMusicName());
        assertEquals("Art", info.getArtistName());
        assertEquals("", info.getErrorMessage());
    }

    @Test
    void testPlayMusicWithMultimedia() {
        loginFree("u1");
        ctrl.createAlbum("VidAlb", "Art");
        ctrl.addMusic("VidSong", "Art", "P", "L", "N", "Rock", "VidAlb", 100, false, "http://url");
        MusicInfo info = ctrl.playMusic("VidSong");
        assertEquals("VidSong", info.getMusicName());
        assertEquals("http://url", info.getUrl());
    }

    @Test
    void testPlayMusicNotFound() {
        loginFree("u1");
        MusicInfo info = ctrl.playMusic("NoSong");
        assertFalse(info.getErrorMessage().isEmpty());
    }

    @Test
    void testMusicExistsTrue() {
        addAlbumAndMusic("ExSong", "ExAlb");
        assertTrue(ctrl.musicExists("ExSong"));
    }

    @Test
    void testMusicExistsFalse() {
        assertFalse(ctrl.musicExists("Nope"));
    }


    @Test
    void testCanCurrentUserSkipFree() {
        loginFree("u1");
        assertFalse(ctrl.canCurrentUserSkip());
    }

    @Test
    void testCanCurrentUserSkipPremium() {
        loginPremium("u1");
        assertTrue(ctrl.canCurrentUserSkip());
    }

    @Test
    void testCanCurrentUserChooseWhatToPlayFree() {
        loginFree("u1");
        assertFalse(ctrl.canCurrentUserChooseWhatToPlay());
    }

    @Test
    void testCanCurrentUserChooseWhatToPlayPremium() {
        loginPremium("u1");
        assertTrue(ctrl.canCurrentUserChooseWhatToPlay());
    }

    @Test
    void testCurrentUserAccessToFavoritesFree() {
        loginFree("u1");
        assertFalse(ctrl.currentUserAccessToFavorites());
    }

    @Test
    void testCurrentUserAccessToFavoritesPremiumTop() {
        loginFree("u1");
        ctrl.getSpotifUM().getCurrentUser().setPlan(new PlanPremiumTop());
        assertTrue(ctrl.currentUserAccessToFavorites());
    }


    @Test
    void testListAllMusicsEmpty() {
        String msg = ctrl.listAllMusics();
        assertTrue(msg.contains("📭"));
    }

    @Test
    void testListAllMusicsWithData() {
        addAlbumAndMusic("SongA", "AlbA");
        String msg = ctrl.listAllMusics();
        assertTrue(msg.contains("SongA"));
    }

    @Test
    void testListAllAlbumsEmpty() {
        String msg = ctrl.listAllAlbums();
        assertTrue(msg.contains("📭"));
    }

    @Test
    void testListAllAlbumsWithData() {
        ctrl.createAlbum("AlbB", "Art");
        String msg = ctrl.listAllAlbums();
        assertTrue(msg.contains("AlbB"));
    }

    @Test
    void testListAllGenresEmpty() {
        String msg = ctrl.listAllGenres();
        assertTrue(msg.contains("📭"));
    }

    @Test
    void testListAllGenresWithData() {
        addAlbumAndMusic("GS", "GA");
        String msg = ctrl.listAllGenres();
        assertTrue(msg.contains("Rock"));
    }


    @Test
    void testGetMostReproducedMusicSuccess() throws Exception {
        addAlbumAndMusic("Hit", "HitAlb");
        ctrl.getSpotifUM().playMusic("Hit");
        String msg = ctrl.getMostReproducedMusic();
        assertTrue(msg.contains("Hit"));
    }

    @Test
    void testGetMostReproducedMusicError() {
        String msg = ctrl.getMostReproducedMusic();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetMostReproducedArtistSuccess() {
        ctrl.getSpotifUM().incrementArtistReproductions("Queen");
        String msg = ctrl.getMostReproducedArtist();
        assertTrue(msg.contains("Queen"));
    }

    @Test
    void testGetMostReproducedArtistError() {
        String msg = ctrl.getMostReproducedArtist();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetUserWithMostPointsSuccess() {
        ctrl.addNewUser("pts", "p@p.com", "a", "pw");
        ctrl.loginWithMessage("pts", "pw");
        ctrl.setPremiumBasePlan();
        ctrl.getSpotifUM().getCurrentUser().getPlan().setPoints(100);
        String msg = ctrl.getUserWithMostPoints();
        assertTrue(msg.contains("pts"));
    }

    @Test
    void testGetUserWithMostPointsError() {
        String msg = ctrl.getUserWithMostPoints();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetMostReproducedGenreSuccess() {
        ctrl.getSpotifUM().incrementGenreReproductions("Rock");
        String msg = ctrl.getMostReproducedGenre();
        assertTrue(msg.contains("Rock"));
    }

    @Test
    void testGetMostReproducedGenreError() {
        String msg = ctrl.getMostReproducedGenre();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetUserWithMostPlaylistsSuccess() {
        loginPremium("u1");
        ctrl.addToCurrentUserPlaylists("P1");
        String msg = ctrl.getUserWithMostPlaylists();
        assertTrue(msg.contains("u1"));
    }

    @Test
    void testGetUserWithMostPlaylistsNoPlaylists() {
        ctrl.addNewUser("noplists", "n@n.com", "a", "pw");
        String msg = ctrl.getUserWithMostPlaylists();
        assertTrue(msg.contains("Não existe utilizadores com playlists") || msg.contains("noplists"));
    }

    @Test
    void testGetUserWithMostPlaylistsError() {
        String msg = ctrl.getUserWithMostPlaylists();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetUserWithMostReproductionsSuccess() {
        loginFree("u1");
        addAlbumAndMusic("RepSong", "RepAlb");
        ctrl.playMusic("RepSong");
        String msg = ctrl.getUserWithMostReproductions();
        assertTrue(msg.contains("u1"));
    }

    @Test
    void testGetUserWithMostReproductionsNoRepros() {
        ctrl.addNewUser("norepro", "n@n.com", "a", "pw");
        String msg = ctrl.getUserWithMostReproductions();
        assertTrue(msg.contains("Não existe utilizadores com reproduções") || msg.contains("norepro"));
    }

    @Test
    void testGetUserWithMostReproductionsError() {
        String msg = ctrl.getUserWithMostReproductions();
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetUserWithMostReproductionsDateRangeSuccess() {
        loginFree("u1");
        addAlbumAndMusic("DateSong", "DateAlb");
        ctrl.playMusic("DateSong");
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        String msg = ctrl.getUserWithMostReproductions(start, end);
        assertTrue(msg.contains("u1"));
    }

    @Test
    void testGetUserWithMostReproductionsDateRangeError() {
        ctrl.addNewUser("u1", "u@u.com", "a", "pw");
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        String msg = ctrl.getUserWithMostReproductions(start, end);
        assertTrue(msg.contains("❌"));
    }

    @Test
    void testGetUserWithMostReproductionsDateRangeNoRepros() {
        ctrl.addNewUser("norepro2", "n2@n.com", "a", "pw");
        ctrl.loginWithMessage("norepro2", "pw");
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        String msg = ctrl.getUserWithMostReproductions(start, end);
        assertTrue(msg.contains("❌") || msg.contains("Não existe"));
    }

    @Test
    void testGetPublicPlaylistCount() {
        ctrl.getSpotifUM().addPlaylist("P1", "admin");
        ctrl.getSpotifUM().addPlaylist("P2", "admin");
        String msg = ctrl.getPublicPlaylistCount();
        assertTrue(msg.contains("2"));
    }

    @Test
    void testGetCurrentUserPlan() {
        loginFree("u1");
        assertEquals("Free", ctrl.getCurrentUserPlan());
    }


    @Test
    void testGetFavoritePlaylistMusicNamesWithReproductions() {
        loginFree("u1");
        addAlbumAndMusic("FavSong1", "FavAlb");
        ctrl.playMusic("FavSong1");
        Optional<List<String>> result = ctrl.getFavoritePlaylistMusicNames(false, 9999);
        assertTrue(result.isPresent());
        assertFalse(result.get().isEmpty());
    }
}
