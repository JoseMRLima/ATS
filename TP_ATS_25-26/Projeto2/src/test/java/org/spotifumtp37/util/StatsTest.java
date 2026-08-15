package org.spotifumtp37.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StatsTest {

    private Map<String, Album> albums;
    private Map<String, User> users;
    private Map<String, Playlist> playlists;

    @BeforeEach
    void setUp() {
        albums = new HashMap<>();
        users = new HashMap<>();
        playlists = new HashMap<>();
    }

    @Test
    void getMostPlayedSong() {
        Song song1 = new Song("Song1", "Artist1", "Publisher1", "Lyrics1", "Notes1", "Rock", 180);
        song1.setTimesPlayed(10);
        
        Song song2 = new Song("Song2", "Artist2", "Publisher2", "Lyrics2", "Notes2", "Pop", 240);
        song2.setTimesPlayed(5);
        
        Song song3 = new Song("Song3", "Artist1", "Publisher1", "Lyrics3", "Notes3", "Rock", 200);
        song3.setTimesPlayed(20);
        
        Album album1 = new Album("Album1", "Artist1", 2020, "Rock", Arrays.asList(song1, song3));
        Album album2 = new Album("Album2", "Artist2", 2021, "Pop", Collections.singletonList(song2));
        
        albums.put("Album1", album1);
        albums.put("Album2", album2);
        
        Song mostPlayed = Stats.getMostPlayedSong(albums);
        assertNotNull(mostPlayed);
        assertEquals("Song3", mostPlayed.getName());
        assertEquals(20, mostPlayed.getTimesPlayed());
        
        assertNull(Stats.getMostPlayedSong(new HashMap<>()));
        
        assertNull(Stats.getMostPlayedSong(null));
    }

    @Test
    void getMostListenedArtist() {
        Song song1 = new Song("Song1", "Artist1", "Publisher1", "Lyrics1", "Notes1", "Rock", 180);
        song1.setTimesPlayed(10);
        
        Song song2 = new Song("Song2", "Artist2", "Publisher2", "Lyrics2", "Notes2", "Pop", 240);
        song2.setTimesPlayed(15);
        
        Song song3 = new Song("Song3", "Artist1", "Publisher1", "Lyrics3", "Notes3", "Rock", 200);
        song3.setTimesPlayed(10);
        
        Album album1 = new Album("Album1", "Artist1", 2020, "Rock", Arrays.asList(song1, song3));
        Album album2 = new Album("Album2", "Artist2", 2021, "Pop", Collections.singletonList(song2));
        
        albums.put("Album1", album1);
        albums.put("Album2", album2);
        
        
        String mostListened = Stats.getMostListenedArtist(albums);
        assertNotNull(mostListened);
        assertEquals("Artist1", mostListened);
        
        assertNull(Stats.getMostListenedArtist(new HashMap<>()));
        
        assertNull(Stats.getMostListenedArtist(null));
    }

    @Test
    void getTopListener() {
        Song song1 = new Song("Song1", "Artist1", "Publisher1", "Lyrics1", "Notes1", "Rock", 180);
        Song song2 = new Song("Song2", "Artist2", "Publisher2", "Lyrics2", "Notes2", "Pop", 240);
        
        List<History> history1 = new ArrayList<>();
        history1.add(new History(song1, LocalDateTime.now()));
        
        List<History> history2 = new ArrayList<>();
        history2.add(new History(song1, LocalDateTime.now()));
        history2.add(new History(song2, LocalDateTime.now()));
        
        User user1 = new User("User1", "user1@email.com", "Address1", new FreePlan(), "pass1", 50, history1);
        User user2 = new User("User2", "user2@email.com", "Address2", new PremiumBase(), "pass2", 100, history2);
        
        users.put("User1", user1);
        users.put("User2", user2);
        
        User topListener = Stats.getTopListener(users);
        assertNotNull(topListener);
        assertEquals("User2", topListener.getName());
        assertEquals(2, topListener.getHistory().size());
        
        assertNull(Stats.getTopListener(new HashMap<>()));
        
        assertNull(Stats.getTopListener(null));
    }

    @Test
    void getUserWithMostPoints() {
        User user1 = new User("User1", "user1@email.com", "Address1", new FreePlan(), "pass1", 50, new ArrayList<>());
        User user2 = new User("User2", "user2@email.com", "Address2", new PremiumBase(), "pass2", 100, new ArrayList<>());
        User user3 = new User("User3", "user3@email.com", "Address3", new PremiumTop(), "pass3", 75, new ArrayList<>());
        
        users.put("User1", user1);
        users.put("User2", user2);
        users.put("User3", user3);
        
        User userWithMostPoints = Stats.getUserWithMostPoints(users);
        assertNotNull(userWithMostPoints);
        assertEquals("User2", userWithMostPoints.getName());
        assertEquals(100, userWithMostPoints.getPontos());
        
        assertNull(Stats.getUserWithMostPoints(new HashMap<>()));
        
        assertNull(Stats.getUserWithMostPoints(null));
    }

    @Test
    void mostListenedGenre() {
        Song rock1 = new Song("Rock1", "Artist1", "Publisher1", "Lyrics1", "Notes1", "Rock", 180);
        Song rock2 = new Song("Rock2", "Artist2", "Publisher2", "Lyrics2", "Notes2", "Rock", 240);
        Song pop1 = new Song("Pop1", "Artist3", "Publisher3", "Lyrics3", "Notes3", "Pop", 200);
        Song jazz1 = new Song("Jazz1", "Artist4", "Publisher4", "Lyrics4", "Notes4", "Jazz", 300);
        
        History h1 = new History(rock1, LocalDateTime.now());
        History h2 = new History(rock2, LocalDateTime.now());
        History h3 = new History(pop1, LocalDateTime.now());
        History h4 = new History(jazz1, LocalDateTime.now());
        History h5 = new History(rock1, LocalDateTime.now()); // Add rock again to make it most listened
        
        List<History> history1 = new ArrayList<>(Arrays.asList(h1, h3));
        List<History> history2 = new ArrayList<>(Arrays.asList(h2, h4, h5));
        
        User user1 = new User("User1", "user1@email.com", "Address1", new FreePlan(), "pass1", 50, history1);
        User user2 = new User("User2", "user2@email.com", "Address2", new PremiumBase(), "pass2", 100, history2);
        
        users.put("User1", user1);
        users.put("User2", user2);
        
        String mostListenedGenre = Stats.mostListenedGenre(users);
        assertNotNull(mostListenedGenre);
        assertEquals("Rock", mostListenedGenre);
        
        assertNull(Stats.mostListenedGenre(new HashMap<>()));
        
        assertNull(Stats.mostListenedGenre(null));
    }

    @Test
    void countPublicPlaylists() {
        User user1 = new User("User1", "user1@email.com", "Address1", new FreePlan(), "pass1", 50, new ArrayList<>());
        User user2 = new User("User2", "user2@email.com", "Address2", new PremiumBase(), "pass2", 100, new ArrayList<>());
        
        Song song = new Song("Song", "Artist", "Publisher", "Lyrics", "Notes", "Rock", 180);
        List<Song> songs = Collections.singletonList(song);
        
        Playlist publicPlaylist1 = new Playlist(user1, "Public1", "Description1", 0, "public", songs);
        Playlist publicPlaylist2 = new Playlist(user2, "Public2", "Description2", 0, "public", songs);
        Playlist privatePlaylist = new Playlist(user1, "Private", "Description3", 0, "private", songs);
        
        playlists.put("Public1", publicPlaylist1);
        playlists.put("Public2", publicPlaylist2);
        playlists.put("Private", privatePlaylist);
        
        long publicCount = Stats.countPublicPlaylists(playlists);
        assertEquals(2, publicCount);
        
        assertEquals(0, Stats.countPublicPlaylists(new HashMap<>()));
        
        assertEquals(0, Stats.countPublicPlaylists(null));
    }

    @Test
    void userWithMostPlaylists() {
        User user1 = new User("User1", "user1@email.com", "Address1", new FreePlan(), "pass1", 50, new ArrayList<>());
        User user2 = new User("User2", "user2@email.com", "Address2", new PremiumBase(), "pass2", 100, new ArrayList<>());
        
        Song song = new Song("Song", "Artist", "Publisher", "Lyrics", "Notes", "Rock", 180);
        List<Song> songs = Collections.singletonList(song);
        
        Playlist playlist1 = new Playlist(user1, "Playlist1", "Description1", 0, "public", songs);
        Playlist playlist2 = new Playlist(user1, "Playlist2", "Description2", 0, "private", songs);
        Playlist playlist3 = new Playlist(user2, "Playlist3", "Description3", 0, "public", songs);
        
        playlists.put("Playlist1", playlist1);
        playlists.put("Playlist2", playlist2);
        playlists.put("Playlist3", playlist3);
        
        User userWithMostPlaylists = Stats.userWithMostPlaylists(playlists);
        assertNotNull(userWithMostPlaylists);
        assertEquals("User1", userWithMostPlaylists.getName());
        
        assertNull(Stats.userWithMostPlaylists(new HashMap<>()));
        
        assertNull(Stats.userWithMostPlaylists(null));
    }

    @Test
    void getMostPlayedSong_SingleSongReturnsIt() {
        Song only = new Song("Only", "Art", "Pub", "L", "N", "Rock", 200);
        only.setTimesPlayed(7);
        Album album = new Album("A", "Art", 2020, "Rock", Collections.singletonList(only));
        albums.put("A", album);

        Song result = Stats.getMostPlayedSong(albums);
        assertEquals("Only", result.getName());
        assertEquals(7, result.getTimesPlayed());
    }

    @Test
    void getMostPlayedSong_TiedPlaysReturnsOneOfThem() {
        Song s1 = new Song("S1", "Art", "Pub", "L", "N", "Rock", 180);
        Song s2 = new Song("S2", "Art", "Pub", "L", "N", "Rock", 180);
        s1.setTimesPlayed(5);
        s2.setTimesPlayed(5);
        albums.put("A", new Album("A", "Art", 2020, "Rock", Arrays.asList(s1, s2)));

        Song result = Stats.getMostPlayedSong(albums);
        assertNotNull(result);
        assertEquals(5, result.getTimesPlayed());
    }

    @Test
    void getMostListenedArtist_SingleArtistReturnsIt() {
        Song s = new Song("S", "SoloArtist", "Pub", "L", "N", "Pop", 200);
        s.setTimesPlayed(3);
        albums.put("A", new Album("A", "SoloArtist", 2020, "Pop", Collections.singletonList(s)));

        assertEquals("SoloArtist", Stats.getMostListenedArtist(albums));
    }

    @Test
    void getTopListener_SingleUserReturnsIt() {
        Song s = new Song("S", "Art", "Pub", "L", "N", "Rock", 200);
        User u = new User("Solo", "s@e.com", "Addr", new FreePlan(), "pw", 0,
                Collections.singletonList(new History(s, LocalDateTime.now())));
        users.put("Solo", u);

        User result = Stats.getTopListener(users);
        assertEquals("Solo", result.getName());
    }

    @Test
    void getUserWithMostPoints_ExactValueVerified() {
        User u1 = new User("A", "a@e.com", "addr", new FreePlan(), "pw", 10, new ArrayList<>());
        User u2 = new User("B", "b@e.com", "addr", new FreePlan(), "pw", 200, new ArrayList<>());
        users.put("A", u1);
        users.put("B", u2);

        User result = Stats.getUserWithMostPoints(users);
        assertEquals("B", result.getName());
        assertEquals(200, result.getPontos(), 0.001);
    }

    @Test
    void countPublicPlaylists_AllPrivateReturnsZero() {
        User u = new User("U", "u@e.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<>());
        Song s = new Song("S", "Art", "Pub", "L", "N", "Rock", 180);
        playlists.put("P1", new Playlist(u, "P1", "D", 0, "private", Collections.singletonList(s)));
        playlists.put("P2", new Playlist(u, "P2", "D", 0, "private", Collections.singletonList(s)));

        assertEquals(0, Stats.countPublicPlaylists(playlists));
    }

    @Test
    void countPublicPlaylists_ExactCountVerified() {
        User u = new User("U", "u@e.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<>());
        Song s = new Song("S", "Art", "Pub", "L", "N", "Rock", 180);
        playlists.put("Pub1", new Playlist(u, "Pub1", "D", 0, "public", Collections.singletonList(s)));
        playlists.put("Pub2", new Playlist(u, "Pub2", "D", 0, "public", Collections.singletonList(s)));
        playlists.put("Priv", new Playlist(u, "Priv", "D", 0, "private", Collections.singletonList(s)));

        assertEquals(2, Stats.countPublicPlaylists(playlists));
    }

    @Test
    void mostListenedGenre_SingleGenreReturnsIt() {
        Song rock = new Song("R", "Art", "Pub", "L", "N", "Rock", 180);
        User u = new User("U", "u@e.com", "addr", new FreePlan(), "pw", 0,
                Arrays.asList(new History(rock, LocalDateTime.now()), new History(rock, LocalDateTime.now())));
        users.put("U", u);

        assertEquals("Rock", Stats.mostListenedGenre(users));
    }

    @Test
    void getTopListenerFromDate_FiltersByDate() {
        Song s = new Song("S", "Art", "Pub", "L", "N", "Rock", 200);
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        LocalDateTime recent = LocalDateTime.now();
        LocalDateTime old = LocalDateTime.now().minusDays(5);

        User u1 = new User("RecentListener", "r@e.com", "addr", new FreePlan(), "pw", 0,
                Arrays.asList(new History(s, recent), new History(s, recent)));
        User u2 = new User("OldListener", "o@e.com", "addr", new FreePlan(), "pw", 0,
                Arrays.asList(new History(s, old), new History(s, old), new History(s, old)));
        users.put("R", u1);
        users.put("O", u2);

        User result = Stats.getTopListenerFromDate(users, cutoff);
        assertEquals("RecentListener", result.getName());
    }

    @Test
    void getTopListenerFromDate_NullDateReturnsNull() {
        users.put("U", new User("U", "u@e.com", "addr", new FreePlan(), "pw", 0, new ArrayList<>()));
        assertNull(Stats.getTopListenerFromDate(users, null));
    }
}