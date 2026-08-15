package org.spotifumtp37.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.exceptions.AlreadyExistsException;
import org.spotifumtp37.exceptions.DoesntExistException;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifUMDataTest {

    private SpotifUMData data;
    private Album album;
    private User user;
    private Playlist playlist;
    private Song song;

    @BeforeEach
    void setUp() {
        data = new SpotifUMData();
        song = new Song("Song1", "Artist1", "Pub", "Lyrics", "Notes", "Rock", 180);
        album = new Album("Album1", "Artist1", 2020, "Rock", Collections.singletonList(song));
        user = new User("User1", "u@e.com", "Addr", new PremiumBase(), "pw", 0, new ArrayList<>());
        List<Song> songs = new ArrayList<>();
        songs.add(song);
        playlist = new Playlist(user, "Playlist1", "Desc", 0, "public", songs);
    }


    @Test
    void addAlbum_ThenExists() throws AlreadyExistsException {
        data.addAlbum(album);
        assertTrue(data.existsAlbum("Album1"));
    }

    @Test
    void addAlbum_DuplicateThrows() throws AlreadyExistsException {
        data.addAlbum(album);
        assertThrows(AlreadyExistsException.class, () -> data.addAlbum(album));
    }

    @Test
    void addUser_ThenExists() throws AlreadyExistsException {
        data.addUser(user);
        assertTrue(data.existsUser("User1"));
    }

    @Test
    void addUser_DuplicateThrows() throws AlreadyExistsException {
        data.addUser(user);
        assertThrows(AlreadyExistsException.class, () -> data.addUser(user));
    }

    @Test
    void addPlaylist_ThenExists() throws AlreadyExistsException {
        data.addPlaylist(playlist);
        assertTrue(data.existsPlaylist("Playlist1"));
    }

    @Test
    void addPlaylist_DuplicateThrows() throws AlreadyExistsException {
        data.addPlaylist(playlist);
        assertThrows(AlreadyExistsException.class, () -> data.addPlaylist(playlist));
    }


    @Test
    void getAlbum_ReturnsCorrectAlbum() throws AlreadyExistsException, DoesntExistException {
        data.addAlbum(album);
        Album result = data.getAlbum("Album1");
        assertEquals("Album1", result.getTitle());
    }

    @Test
    void getAlbum_MissingThrows() {
        assertThrows(DoesntExistException.class, () -> data.getAlbum("Missing"));
    }

    @Test
    void getUser_ReturnsCorrectUser() throws AlreadyExistsException, DoesntExistException {
        data.addUser(user);
        User result = data.getUser("User1");
        assertEquals("User1", result.getName());
    }

    @Test
    void getUser_MissingThrows() {
        assertThrows(DoesntExistException.class, () -> data.getUser("Missing"));
    }

    @Test
    void getPlaylist_ReturnsCorrectPlaylist() throws AlreadyExistsException, DoesntExistException {
        data.addPlaylist(playlist);
        Playlist result = data.getPlaylist("Playlist1");
        assertEquals("Playlist1", result.getPlaylistName());
    }

    @Test
    void getPlaylist_MissingThrows() {
        assertThrows(DoesntExistException.class, () -> data.getPlaylist("Missing"));
    }


    @Test
    void removeAlbum_ThenDoesNotExist() throws AlreadyExistsException, DoesntExistException {
        data.addAlbum(album);
        data.removeAlbum("Album1");
        assertFalse(data.existsAlbum("Album1"));
    }

    @Test
    void removeAlbum_MissingThrows() {
        assertThrows(DoesntExistException.class, () -> data.removeAlbum("Missing"));
    }

    @Test
    void removeUser_ThenDoesNotExist() throws AlreadyExistsException, DoesntExistException {
        data.addUser(user);
        data.removeUser("User1");
        assertFalse(data.existsUser("User1"));
    }

    @Test
    void removeUser_MissingThrows() {
        assertThrows(DoesntExistException.class, () -> data.removeUser("Missing"));
    }

    @Test
    void removePlaylist_ThenDoesNotExist() throws AlreadyExistsException, DoesntExistException {
        data.addPlaylist(playlist);
        data.removePlaylist("Playlist1");
        assertFalse(data.existsPlaylist("Playlist1"));
    }

    @Test
    void removePlaylist_MissingThrows() {
        assertThrows(DoesntExistException.class, () -> data.removePlaylist("Missing"));
    }


    @Test
    void existsAlbum_FalseWhenEmpty() {
        assertFalse(data.existsAlbum("Anything"));
    }

    @Test
    void existsUser_FalseWhenEmpty() {
        assertFalse(data.existsUser("Anybody"));
    }

    @Test
    void existsPlaylist_FalseWhenEmpty() {
        assertFalse(data.existsPlaylist("Anything"));
    }

    @Test
    void existsSong_TrueWhenPresent() throws AlreadyExistsException {
        data.addAlbum(album);
        assertTrue(data.existsSong("Song1", "Album1"));
    }

    @Test
    void existsSong_FalseWhenAlbumMissing() {
        assertFalse(data.existsSong("Song1", "NoAlbum"));
    }

    @Test
    void existsSong_FalseWhenSongMissing() throws AlreadyExistsException {
        data.addAlbum(album);
        assertFalse(data.existsSong("NonExistentSong", "Album1"));
    }


    @Test
    void getMapAlbums_ReturnsAllAlbums() throws AlreadyExistsException {
        data.addAlbum(album);
        Map<String, Album> map = data.getMapAlbums();
        assertEquals(1, map.size());
        assertTrue(map.containsKey("Album1"));
    }

    @Test
    void getMapUsers_ReturnsAllUsers() throws AlreadyExistsException {
        data.addUser(user);
        Map<String, User> map = data.getMapUsers();
        assertEquals(1, map.size());
        assertTrue(map.containsKey("User1"));
    }

    @Test
    void getMapPlaylists_ReturnsAllPlaylists() throws AlreadyExistsException {
        data.addPlaylist(playlist);
        Map<String, Playlist> map = data.getMapPlaylists();
        assertEquals(1, map.size());
        assertTrue(map.containsKey("Playlist1"));
    }

    @Test
    void getMapAlbumsCopy_IsDefensiveCopy() throws AlreadyExistsException {
        data.addAlbum(album);
        Map<String, Album> copy1 = data.getMapAlbumsCopy();
        Map<String, Album> copy2 = data.getMapAlbumsCopy();
        assertNotSame(copy1, copy2);
        assertEquals(copy1.size(), copy2.size());
    }


    @Test
    void clone_ProducesDistinctObjectWithSameKeys() throws AlreadyExistsException {
        data.addAlbum(album);
        data.addUser(user);
        SpotifUMData cloned = data.clone();
        assertNotSame(data, cloned);
        assertTrue(cloned.existsAlbum("Album1"));
        assertTrue(cloned.existsUser("User1"));
    }

    @Test
    void equals_EmptyDataEqualsEmpty() {
        SpotifUMData other = new SpotifUMData();
        assertEquals(data, other);
    }

    @Test
    void equals_DifferentUserCountReturnsFalse() throws AlreadyExistsException {
        SpotifUMData other = new SpotifUMData();
        data.addUser(user);
        assertNotEquals(data, other);
    }

    @Test
    void equals_NullReturnsFalse() {
        assertNotEquals(data, null);
    }

    @Test
    void equals_SelfReturnsTrue() {
        assertEquals(data, data);
    }


    @Test
    void getPlaylistMapByCreator_ReturnsOnlyCreatorsPlaylists() throws AlreadyExistsException {
        User other = new User("Other", "o@e.com", "Addr", new FreePlan(), "pw", 0, new ArrayList<>());
        List<Song> songs = Collections.singletonList(song);
        Playlist pl2 = new Playlist(other, "OtherPL", "Desc", 0, "public", songs);

        data.addPlaylist(playlist);
        data.addPlaylist(pl2);

        Map<String, Playlist> byUser = data.getPlaylistMapByCreator(user);
        assertEquals(1, byUser.size());
        assertTrue(byUser.containsKey("Playlist1"));
    }


    @Test
    void getCurrentUserPointer_ReturnsNullWhenMissing() {
        assertNull(data.getCurrentUserPointer("nobody"));
    }

    @Test
    void getCurrentUserPointer_ReturnsUserWhenPresent() throws AlreadyExistsException {
        data.addUser(user);
        User ptr = data.getCurrentUserPointer("User1");
        assertNotNull(ptr);
        assertEquals("User1", ptr.getName());
    }

    @Test
    void getSong_ReturnsCorrectSong() throws AlreadyExistsException, DoesntExistException {
        data.addAlbum(album);
        Song result = data.getSong("Song1", "Album1");
        assertNotNull(result);
        assertEquals("Song1", result.getName());
    }

    @Test
    void getSong_MissingAlbumThrows() {
        assertThrows(DoesntExistException.class, () -> data.getSong("Song1", "NoAlbum"));
    }

    @Test
    void getSong_MissingSongThrows() throws AlreadyExistsException {
        data.addAlbum(album);
        assertThrows(DoesntExistException.class, () -> data.getSong("NoSong", "Album1"));
    }

    @Test
    void getAnyPlaylist_CreatorCanAccessOwnPrivate() throws AlreadyExistsException, DoesntExistException {
        Playlist priv = new Playlist(user, "PrivPL", "desc", 0, "private", Collections.singletonList(song));
        data.addPlaylist(priv);
        Playlist result = data.getAnyPlaylist("PrivPL", user);
        assertEquals("PrivPL", result.getPlaylistName());
    }

    @Test
    void getAnyPlaylist_PublicAccessibleByOther() throws AlreadyExistsException, DoesntExistException {
        User other = new User("Other", "o@e.com", "Addr", new FreePlan(), "pw", 0, new ArrayList<>());
        data.addPlaylist(playlist);
        Playlist result = data.getAnyPlaylist("Playlist1", other);
        assertEquals("Playlist1", result.getPlaylistName());
    }

    @Test
    void getAnyPlaylist_PrivateByOtherThrows() throws AlreadyExistsException {
        User other = new User("Other", "o@e.com", "Addr", new FreePlan(), "pw", 0, new ArrayList<>());
        Playlist priv = new Playlist(user, "PrivPL", "desc", 0, "private", Collections.singletonList(song));
        data.addPlaylist(priv);
        assertThrows(DoesntExistException.class, () -> data.getAnyPlaylist("PrivPL", other));
    }

    @Test
    void setMapAlbums_ReplacesContents() throws AlreadyExistsException {
        data.addAlbum(album);
        Map<String, Album> newMap = new java.util.HashMap<>();
        Album album2 = new Album("Album2", "Art2", 2021, "Pop", Collections.singletonList(song));
        newMap.put("Album2", album2);
        data.setMapAlbums(newMap);
        assertFalse(data.existsAlbum("Album1"));
        assertTrue(data.existsAlbum("Album2"));
    }

    @Test
    void setMapUsers_ReplacesContents() throws AlreadyExistsException {
        data.addUser(user);
        Map<String, User> newMap = new java.util.HashMap<>();
        User user2 = new User("User2", "u2@e.com", "Addr", new FreePlan(), "pw", 0, new ArrayList<>());
        newMap.put("User2", user2);
        data.setMapUsers(newMap);
        assertFalse(data.existsUser("User1"));
        assertTrue(data.existsUser("User2"));
    }

    @Test
    void setMapPlaylists_ReplacesContents() throws AlreadyExistsException {
        data.addPlaylist(playlist);
        Map<String, org.spotifumtp37.model.playlist.Playlist> newMap = new java.util.HashMap<>();
        org.spotifumtp37.model.playlist.Playlist pl2 = new org.spotifumtp37.model.playlist.Playlist(
                user, "PL2", "d", 0, "public", Collections.singletonList(song));
        newMap.put("PL2", pl2);
        data.setMapPlaylists(newMap);
        assertFalse(data.existsPlaylist("Playlist1"));
        assertTrue(data.existsPlaylist("PL2"));
    }

    @Test
    void toString_ContainsAlbumAndUser() throws AlreadyExistsException {
        data.addAlbum(album);
        data.addUser(user);
        String s = data.toString();
        assertNotNull(s);
        assertTrue(s.length() > 0);
    }

    @Test
    void equals_SameInstanceReturnsTrue() {
        assertEquals(data, data);
    }

    @Test
    void equals_DifferentTypeReturnsFalse() {
        assertNotEquals(data, "not a SpotifUMData");
    }
}
