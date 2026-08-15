package org.Model.User;

import org.Exceptions.AlreadyExistsException;
import org.Exceptions.NotFoundException;
import org.Exceptions.NoPremissionException;
import org.Model.Music.Music;
import org.Model.Plan.PlanFree;
import org.Model.Playlist.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class UserTest {

    private User user;

    private Music music;
    private final String NAME = "Bohemian Rhapsody";
    private final String INTERPRETER = "Queen";
    private final String PUBLISHER = "EMI";
    private final String LYRICS = "Is this the real life? Is this just fantasy?";
    private final String MUSICAL_FIGURES = "A-B-C-D";
    private final String GENRE = "Rock";
    private final String ALBUM = "A Night at the Opera";
    private final int DURATION = 355;
    private final boolean EXPLICIT = false;
    
    @BeforeEach
    public void setUp() {
        // Make sure the music object is properly initialized
        music = new Music(NAME, INTERPRETER, PUBLISHER, LYRICS, MUSICAL_FIGURES, 
                          GENRE, ALBUM, DURATION, EXPLICIT);
        user = new User("testUser", "test@example.com", "123 Test St", "password123");
    }

    @Test
    void testUserConstructor() {
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("123 Test St", user.getAdress());
        assertEquals("password123", user.getPassword());
        assertTrue(user.getPlan() instanceof PlanFree);
        assertTrue(user.getPlaylists().isEmpty());
        assertTrue(user.getMusicReproductions().isEmpty());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void testSetAndGetAdress() {
        user.setAdress("456 New St");
        assertEquals("456 New St", user.getAdress());
    }

    @Test
    void testSetAndGetPassword() {
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void testAddPlaylist() {
        user.setPlan(new PlanFree() {
            @Override
            public boolean canAccessLibrary() {
                return true;
            }
        });

        user.addPlaylist("My Playlist");
        assertEquals(1, user.getPlaylists().size());
        assertEquals("My Playlist", user.getPlaylists().get(0).getName());
    }

    @Test
    void testAddPlaylistThrowsExceptionForFreeUser() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> user.addPlaylist("My Playlist"));
        assertEquals("Utilizadores Free não podem adicionar playlists.", exception.getMessage());
    }

    @Test
    void testAddMusicReproduction() {
        user.addMusicReproduction(music);
        assertEquals(1, user.getMusicReproductions().size());
        assertEquals(music, user.getMusicReproductions().get(0).getMusic());
    }

    @Test
    void testAddMusicToPlaylist() throws NotFoundException, AlreadyExistsException, NoPremissionException {
        user.setPlan(new PlanFree() {
            @Override
            public boolean canAccessLibrary() {
                return true;
            }
        });

        Playlist playlist = new Playlist("My Playlist", user.getUsername());
        user.addPlaylistToLibrary(playlist);

        user.addMusicPlaylist(playlist.getId(), music);

        Playlist retrievedPlaylist = user.getPlaylistById(playlist.getId());
        assertTrue(retrievedPlaylist.getMusics().contains(music));
    }

    @Test
    void testAddMusicToPlaylistThrowsNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class, () -> user.addMusicPlaylist(1, music));
        assertEquals("Não encontrado: 1", exception.getMessage());
    }

    @Test
    void testRemoveMusicFromPlaylist() throws NotFoundException, NoPremissionException {
        user.setPlan(new PlanFree() {
            @Override
            public boolean canAccessLibrary() {
                return true;
            }
        });

        Playlist playlist = new Playlist("My Playlist", user.getUsername());
        try {
            user.addPlaylistToLibrary(playlist);
            playlist.addMusic(music);
        } catch (AlreadyExistsException e) {
            fail("Exception should not have been thrown: " + e.getMessage());
        }

        user.removeMusicFromPlaylist(music, playlist.getId());
        assertFalse(playlist.getMusics().contains(music));
    }

    @Test
    void testRemoveMusicFromPlaylistThrowsNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class, () -> user.removeMusicFromPlaylist(music, 1));
        assertEquals("Não encontrado: 1", exception.getMessage());
    }

    @Test
    void testGetMusicReproductionsCount() {
        user.addMusicReproduction(music);

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        assertEquals(1, user.getMusicReproductionsCount(startDate, endDate));
    }

    @Test
    void testClone() {
        User clonedUser = user.clone();
        assertEquals(user, clonedUser);
        assertNotSame(user, clonedUser);
    }

    @Test
    void testEquals_sameObject() {
        assertEquals(user, user);
    }

    @Test
    void testEquals_null() {
        assertNotEquals(user, null);
    }

    @Test
    void testEquals_differentType() {
        assertNotEquals(user, "string");
    }

    @Test
    void testEquals_differentUsername() {
        User other = new User("other", "test@example.com", "123 Test St", "password123");
        assertNotEquals(user, other);
    }

    @Test
    void testEquals_allSame() {
        User same = new User("testUser", "test@example.com", "123 Test St", "password123");
        assertEquals(user, same);
    }

    @Test
    void testHasLibrary_freeUser() {
        assertFalse(user.hasLibrary());
    }

    @Test
    void testHasLibrary_premiumUser() {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        assertTrue(user.hasLibrary());
    }

    @Test
    void testNamePlaylists_freeUserThrows() {
        assertThrows(UnsupportedOperationException.class, () -> user.namePlaylists());
    }

    @Test
    void testNamePlaylists_premiumUser() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        user.addPlaylist("Rock Classics");
        String result = user.namePlaylists();
        assertTrue(result.contains("Rock Classics"));
    }

    @Test
    void testGetUserPlaylistCount() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        assertEquals(0, user.getUserPlaylistCount());
        user.addPlaylist("Playlist A");
        user.addPlaylist("Playlist B");
        assertEquals(2, user.getUserPlaylistCount());
    }

    @Test
    void testChangePlaylistAutor() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        user.addPlaylist("My Playlist");
        user.changePlaylistAutor("newName");
        // após mudança de autor, a playlist deve ter o novo autor
        // getUserPlaylistCount conta pelo username antigo, por isso deve ser 0
        assertEquals(0, user.getUserPlaylistCount());
    }

    @Test
    void testAddMusicReproduction_multipleEntries() {
        Music m2 = new Music("Other Song", "Artist", "Pub", "Lyrics", "notes", "Pop", "Album2", 200, false);
        user.addMusicReproduction(music);
        user.addMusicReproduction(m2);
        assertEquals(2, user.getMusicReproductions().size());
    }

    @Test
    void testGetMusicReproductionsCount_outsideRange() {
        user.addMusicReproduction(music);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(2);
        assertEquals(0, user.getMusicReproductionsCount(startDate, endDate));
    }

    @Test
    void testAddPlaylistToLibrary_duplicate() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        Playlist p = new Playlist("Shared", "otherUser");
        user.addPlaylistToLibrary(p);
        assertThrows(org.Exceptions.AlreadyExistsException.class, () -> user.addPlaylistToLibrary(p));
    }

    @Test
    void testGetPlaylistById_freeUserThrows() {
        assertThrows(UnsupportedOperationException.class, () -> user.getPlaylistById(1));
    }

    @Test
    void testGetPlaylistById_notFound() {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        assertThrows(NotFoundException.class, () -> user.getPlaylistById(999));
    }

    @Test
    void testAddMusicToPlaylist_noPremission() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        Playlist othersPlaylist = new Playlist("Other", "otherUser");
        user.addPlaylistToLibrary(othersPlaylist);
        assertThrows(NoPremissionException.class,
                () -> user.addMusicPlaylist(othersPlaylist.getId(), music));
    }

    @Test
    void testRemoveMusicFromPlaylist_musicNotFound() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        Playlist p = new Playlist("Mine", user.getUsername());
        user.addPlaylistToLibrary(p);
        Music absent = new Music("NotThere", "X", "X", "X", "X", "X", "X", 10, false);
        assertThrows(NotFoundException.class,
                () -> user.removeMusicFromPlaylist(absent, p.getId()));
    }

    @Test
    void testAddPoints_delegatesToPlan() {
        org.Model.Plan.PlanPremiumBase plan = new org.Model.Plan.PlanPremiumBase();
        plan.setPoints(0);
        user.setPlan(plan);
        user.addPoints();
        assertEquals(10, user.getPlan().getPoints());
    }

    @Test
    void testToString_containsUsername() {
        assertTrue(user.toString().contains("testUser"));
    }

    @Test
    void testDefaultConstructor() {
        User empty = new User();
        assertEquals("", empty.getUsername());
        assertTrue(empty.getPlan() instanceof PlanFree);
        assertTrue(empty.getPlaylists().isEmpty());
    }

    @Test
    void testFullArgumentConstructor() {
        List<org.Model.Playlist.Playlist> playlists = new java.util.ArrayList<>();
        List<org.Model.Music.MusicReproduction> repros = new java.util.ArrayList<>();
        User u = new User("full", "f@f.com", "addr", "pw", new PlanFree(), playlists, repros);
        assertEquals("full", u.getUsername());
        assertEquals("f@f.com", u.getEmail());
        assertTrue(u.getPlaylists().isEmpty());
        assertTrue(u.getMusicReproductions().isEmpty());
    }

    @Test
    void testSetPlaylists() {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        user.addPlaylist("A");
        List<org.Model.Playlist.Playlist> current = user.getPlaylists();
        User u2 = new User();
        u2.setPlaylists(current);
        assertEquals(1, u2.getPlaylists().size());
    }

    @Test
    void testSetMusicReproductions() {
        user.addMusicReproduction(music);
        List<org.Model.Music.MusicReproduction> repros = user.getMusicReproductions();
        User u2 = new User();
        u2.setMusicReproductions(repros);
        assertEquals(1, u2.getMusicReproductions().size());
    }

    @Test
    void testGetMusicReproductionsCount_exactBoundary() {
        user.addMusicReproduction(music);
        // isAfter/isBefore are exclusive — same day not counted
        LocalDate today = LocalDate.now();
        assertEquals(0, user.getMusicReproductionsCount(today, today));
    }

    @Test
    void testRemoveMusicFromPlaylist_noPermission() throws AlreadyExistsException {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        org.Model.Playlist.Playlist othersPlaylist = new org.Model.Playlist.Playlist("Other", "otherUser");
        othersPlaylist.addMusic(music);
        user.addPlaylistToLibrary(othersPlaylist);
        assertThrows(NoPremissionException.class,
                () -> user.removeMusicFromPlaylist(music, othersPlaylist.getId()));
    }

    @Test
    void testAddMusicPlaylist_alreadyExists() throws Exception {
        user.setPlan(new org.Model.Plan.PlanPremiumBase());
        user.addPlaylist("Mine");
        int id = user.getPlaylists().get(0).getId();
        user.addMusicPlaylist(id, music);
        assertThrows(AlreadyExistsException.class, () -> user.addMusicPlaylist(id, music));
    }

    @Test
    void testEquals_differentPassword() {
        User other = new User("testUser", "test@example.com", "123 Test St", "differentPassword");
        assertNotEquals(user, other);
    }

    @Test
    void testEquals_differentAddress() {
        User other = new User("testUser", "test@example.com", "456 Other St", "password123");
        assertNotEquals(user, other);
    }

    @Test
    void testEquals_differentEmail() {
        User other = new User("testUser", "other@example.com", "123 Test St", "password123");
        assertNotEquals(user, other);
    }

    @Test
    void testEqualsSelf() {
        assertTrue(user.equals(user));
    }

    @Test
    void testEqualsNull() {
        assertFalse(user.equals(null));
    }

    @Test
    void testEqualsWrongType() {
        assertFalse(user.equals("not a user"));
    }
}