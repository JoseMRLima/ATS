package org.spotifumtp37;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.ExplicitSong;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-Based Tests para o Projeto2 usando jqwik.
 *
 * Cada @Property é executada com múltiplos inputs gerados automaticamente pelo jqwik,
 * verificando invariantes e contratos do domínio em vez de casos concretos.
 */
class PropertyBasedTest {

    // ─── Song ─────────────────────────────────────────────────────────────────

    /**
     * Propriedade: reproduzir uma música n vezes incrementa o contador exactamente n vezes.
     */
    @Property
    void songIncrementTimesPlayedCountsExactly(
            @ForAll @IntRange(min = 0, max = 50) int n) {
        Song song = new Song("Title", "Artist", "Publisher", "Lyrics", "Notes", "Pop", 180);
        for (int i = 0; i < n; i++) {
            song.incrementTimesPlayed();
        }
        assertEquals(n, song.getTimesPlayed());
    }

    /**
     * Propriedade: cada chamada a incrementTimesPlayed aumenta o contador exactamente 1.
     */
    @Property
    void songIncrementTimesPlayedIsAlwaysPlusOne(
            @ForAll @IntRange(min = 0, max = 100) int initial) {
        Song song = new Song("Title", "Artist", "Publisher", "Lyrics", "Notes", "Rock", 200);
        song.setTimesPlayed(initial);
        song.incrementTimesPlayed();
        assertEquals(initial + 1, song.getTimesPlayed());
    }

    /**
     * Propriedade: o clone de uma Song é igual ao original mas não é o mesmo objecto.
     */
    @Property
    void songCloneIsEqualButDistinct(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String name,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String artist,
            @ForAll @IntRange(min = 1, max = 600) int duration) {
        Song original = new Song(name, artist, "Publisher", "Lyrics", "Notes", "Pop", duration);
        Song clone = original.clone();
        assertEquals(original, clone);
        assertNotSame(original, clone);
    }

    /**
     * Propriedade: uma Song regular nunca é explícita nem multimédia.
     */
    @Property
    void regularSongIsNeverExplicitOrMultimedia(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String name,
            @ForAll @IntRange(min = 1, max = 600) int duration) {
        Song song = new Song(name, "Artist", "Publisher", "Lyrics", "Notes", "Rock", duration);
        assertFalse(song.isExplicit(), "Regular Song nunca deve ser explícita");
        assertFalse(song.isMultimedia(), "Regular Song nunca deve ser multimédia");
    }

    /**
     * Propriedade: uma ExplicitSong é sempre explícita e nunca multimédia.
     */
    @Property
    void explicitSongIsAlwaysExplicitNeverMultimedia(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String name,
            @ForAll @IntRange(min = 1, max = 600) int duration) {
        ExplicitSong song = new ExplicitSong(name, "Artist", "Publisher", "Lyrics", "Notes", "HipHop", duration);
        assertTrue(song.isExplicit(), "ExplicitSong deve ser sempre explícita");
        assertFalse(song.isMultimedia(), "ExplicitSong nunca deve ser multimédia");
    }

    /**
     * Propriedade: uma música adicionada como multimédia via Album.addSong() é sempre
     * multimédia e nunca explícita.
     */
    @Property
    void albumMultimediaSongIsAlwaysMultimediaNeverExplicit(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String name,
            @ForAll @IntRange(min = 1, max = 600) int duration) {
        Album album = new Album("TestAlbum", "Artist", 2020, "Pop", new ArrayList<>());
        album.addSong(name, "Publisher", "Lyrics", "Notes", "Pop", duration, false, true, "https://video.example.com");
        Song song = album.getSongs().get(0);
        assertTrue(song.isMultimedia(), "Música adicionada como multimédia deve ser sempre multimédia");
        assertFalse(song.isExplicit(), "Música multimédia nunca deve ser explícita");
    }

    /**
     * Propriedade: a duração de uma Song é sempre a que foi definida no construtor.
     */
    @Property
    void songDurationMatchesConstructorValue(
            @ForAll @IntRange(min = 1, max = 7200) int duration) {
        Song song = new Song("Title", "Artist", "Publisher", "Lyrics", "Notes", "Jazz", duration);
        assertEquals(duration, song.getDurationInSeconds());
    }

    // ─── SubscriptionPlan — addPoints ─────────────────────────────────────────

    /**
     * Propriedade: FreePlan.addPoints(p) == p + 5, para qualquer p ≥ 0.
     */
    @Property
    void freePlanAddsFivePointsToAny(
            @ForAll @DoubleRange(min = 0.0, max = 100_000.0) double points) {
        FreePlan plan = new FreePlan();
        assertEquals(points + 5.0, plan.addPoints(points), 1e-9);
    }

    /**
     * Propriedade: PremiumBase.addPoints(p) == p + 10, para qualquer p ≥ 0.
     */
    @Property
    void premiumBaseAddsTenPointsToAny(
            @ForAll @DoubleRange(min = 0.0, max = 100_000.0) double points) {
        PremiumBase plan = new PremiumBase();
        assertEquals(points + 10.0, plan.addPoints(points), 1e-9);
    }

    /**
     * Propriedade: PremiumTop.addPoints(p) == 1.025 * p, para qualquer p ≥ 0.
     */
    @Property
    void premiumTopAppliesTwoPointFivePercent(
            @ForAll @DoubleRange(min = 0.0, max = 100_000.0) double points) {
        PremiumTop plan = new PremiumTop();
        assertEquals(1.025 * points, plan.addPoints(points), 1e-6);
    }

    /**
     * Propriedade: FreePlan.addPoints nunca decresce os pontos.
     */
    @Property
    void freePlanPointsNeverDecrease(
            @ForAll @DoubleRange(min = 0.0, max = 100_000.0) double points) {
        FreePlan plan = new FreePlan();
        assertTrue(plan.addPoints(points) > points);
    }

    /**
     * Propriedade: PremiumBase.addPoints nunca decresce os pontos.
     */
    @Property
    void premiumBasePointsNeverDecrease(
            @ForAll @DoubleRange(min = 0.0, max = 100_000.0) double points) {
        PremiumBase plan = new PremiumBase();
        assertTrue(plan.addPoints(points) > points);
    }

    // ─── SubscriptionPlan — permissões (invariantes determinísticos) ──────────

    /**
     * Propriedade: FreePlan nunca permite criar playlists, navegar ou aceder a favoritos.
     */
    @Property(tries = 1)
    void freePlanForbidsAllPremiumFeatures() {
        FreePlan plan = new FreePlan();
        assertFalse(plan.canCreatePlaylist());
        assertFalse(plan.canBrowsePlaylist());
        assertFalse(plan.canAccessFavouritesList());
    }

    /**
     * Propriedade: PremiumBase permite criar e navegar playlists, mas não aceder a favoritos.
     */
    @Property(tries = 1)
    void premiumBaseAllowsPlaylistsButNotFavourites() {
        PremiumBase plan = new PremiumBase();
        assertTrue(plan.canCreatePlaylist());
        assertTrue(plan.canBrowsePlaylist());
        assertFalse(plan.canAccessFavouritesList());
    }

    /**
     * Propriedade: PremiumTop permite todas as funcionalidades.
     */
    @Property(tries = 1)
    void premiumTopAllowsAllFeatures() {
        PremiumTop plan = new PremiumTop();
        assertTrue(plan.canCreatePlaylist());
        assertTrue(plan.canBrowsePlaylist());
        assertTrue(plan.canAccessFavouritesList());
    }

    // ─── Album ────────────────────────────────────────────────────────────────

    /**
     * Propriedade: getTotalDuration() == soma das durações de todas as músicas adicionadas.
     */
    @Property
    void albumTotalDurationEqualsSumOfSongDurations(
            @ForAll @Size(min = 1, max = 10) List<@IntRange(min = 30, max = 600) Integer> durations) {
        Album album = new Album("TestAlbum", "Artist", 2020, "Rock", new ArrayList<>());
        int expected = 0;
        for (int i = 0; i < durations.size(); i++) {
            int d = durations.get(i);
            album.addSong("Song" + i, "Publisher", "Lyrics", "Notes", "Rock", d, false, false, "");
            expected += d;
        }
        assertEquals(expected, album.getTotalDuration());
    }

    /**
     * Propriedade: após n addSong com nomes distintos, getSongs().size() == n.
     */
    @Property
    void albumSizeAfterAdditionsMatchesCount(
            @ForAll @IntRange(min = 0, max = 15) int n) {
        Album album = new Album("TestAlbum", "Artist", 2020, "Pop", new ArrayList<>());
        for (int i = 0; i < n; i++) {
            album.addSong("UniqueTitle_" + i, "Publisher", "Lyrics", "Notes", "Pop", 180 + i, false, false, "");
        }
        assertEquals(n, album.getSongs().size());
    }

    /**
     * Propriedade: getTotalDuration() >= 0 para qualquer número de músicas.
     */
    @Property
    void albumTotalDurationIsNonNegative(
            @ForAll @IntRange(min = 0, max = 10) int n) {
        Album album = new Album("TestAlbum", "Artist", 2020, "Jazz", new ArrayList<>());
        for (int i = 0; i < n; i++) {
            album.addSong("Track" + i, "Publisher", "Lyrics", "Notes", "Jazz", 120, false, false, "");
        }
        assertTrue(album.getTotalDuration() >= 0);
    }

    /**
     * Propriedade: um álbum sem músicas tem duração total zero.
     */
    @Property(tries = 1)
    void emptyAlbumHasZeroTotalDuration() {
        Album album = new Album("Empty", "NoArtist", 2000, "Unknown", new ArrayList<>());
        assertEquals(0, album.getTotalDuration());
    }

    // ─── User — acumulação de pontos ─────────────────────────────────────────

    /**
     * Propriedade: com FreePlan, n chamadas a addPoints() → pontos == inicial + 5*n.
     */
    @Property
    void userFreePlanPointsAccumulateLinearly(
            @ForAll @DoubleRange(min = 0.0, max = 1_000.0) double initial,
            @ForAll @IntRange(min = 0, max = 30) int plays) {
        User user = new User("user", "u@test.com", "Addr", new FreePlan(), "pass", initial, new ArrayList<>());
        for (int i = 0; i < plays; i++) {
            user.addPoints();
        }
        assertEquals(initial + 5.0 * plays, user.getPontos(), 1e-6);
    }

    /**
     * Propriedade: com PremiumBase, n chamadas a addPoints() → pontos == inicial + 10*n.
     */
    @Property
    void userPremiumBasePointsAccumulateLinearly(
            @ForAll @DoubleRange(min = 0.0, max = 1_000.0) double initial,
            @ForAll @IntRange(min = 0, max = 30) int plays) {
        User user = new User("user", "u@test.com", "Addr", new PremiumBase(), "pass", initial, new ArrayList<>());
        for (int i = 0; i < plays; i++) {
            user.addPoints();
        }
        assertEquals(initial + 10.0 * plays, user.getPontos(), 1e-6);
    }

    /**
     * Propriedade: com PremiumTop, n chamadas a addPoints() → pontos == inicial * 1.025^n.
     */
    @Property
    void userPremiumTopPointsGrowExponentially(
            @ForAll @DoubleRange(min = 0.0, max = 1_000.0) double initial,
            @ForAll @IntRange(min = 0, max = 10) int plays) {
        User user = new User("user", "u@test.com", "Addr", new PremiumTop(), "pass", initial, new ArrayList<>());
        double expected = initial;
        for (int i = 0; i < plays; i++) {
            expected = 1.025 * expected;
        }
        for (int i = 0; i < plays; i++) {
            user.addPoints();
        }
        assertEquals(expected, user.getPontos(), 1e-4);
    }

    /**
     * Propriedade: os pontos de um utilizador nunca diminuem após addPoints() com qualquer plano.
     */
    @Property
    void userPointsNeverDecreaseAfterAddPoints(
            @ForAll @DoubleRange(min = 0.0, max = 10_000.0) double initial) {
        User userFree = new User("a", "a@a.com", "Addr", new FreePlan(), "p", initial, new ArrayList<>());
        User userBase = new User("b", "b@b.com", "Addr", new PremiumBase(), "p", initial, new ArrayList<>());
        User userTop  = new User("c", "c@c.com", "Addr", new PremiumTop(),  "p", initial, new ArrayList<>());

        double before = initial;
        userFree.addPoints();
        userBase.addPoints();
        userTop.addPoints();

        assertTrue(userFree.getPontos() >= before);
        assertTrue(userBase.getPontos() >= before);
        assertTrue(userTop.getPontos() >= before);
    }
}
