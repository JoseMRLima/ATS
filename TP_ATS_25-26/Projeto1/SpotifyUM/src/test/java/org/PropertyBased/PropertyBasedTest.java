package org.PropertyBased;

import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Plan.Plan;
import org.Model.Plan.PlanFree;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Plan.PlanPremiumTop;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PropertyBasedTest {


    @ParameterizedTest
    @CsvFileSource(resources = "/property-data/plan_points.csv", numLinesToSkip = 1)
    void planPointsProperty(String planName, int initialPoints, int numCalls, int expectedPoints) {
        Plan plan = createPlan(planName);
        plan.setPoints(initialPoints);
        for (int i = 0; i < numCalls; i++) {
            plan.addPoints();
        }
        assertEquals(expectedPoints, plan.getPoints());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/property-data/music_reproductions.csv", numLinesToSkip = 1)
    void musicReproductionsProperty(String musicName, int duration, int numPlays, int expectedReproductions) {
        Music music = new Music(musicName, "Artist", "Publisher", "Lyrics here",
                                "Notes", "Rock", "Album", duration, false);
        for (int i = 0; i < numPlays; i++) {
            music.play();
        }
        assertEquals(expectedReproductions, music.getReproductions());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/property-data/album_size.csv", numLinesToSkip = 1)
    void albumSizeProperty(String albumName, String artist, int numMusics, int expectedSize) {
        Album album = new Album(albumName, artist);
        for (int i = 0; i < numMusics; i++) {
            Music m = new Music("song_" + i, artist, "Publisher", "Lyrics",
                                "Notes", "Rock", albumName, 180, false);
            album.addMusic(m);
        }
        assertEquals(expectedSize, album.getMusics().size());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/property-data/plan_permissions.csv", numLinesToSkip = 1)
    void planPermissionsProperty(String planName,
                                 boolean canAccessLibrary,
                                 boolean canSkip,
                                 boolean canChooseWhatToPlay,
                                 boolean hasAccessToFavorites) {
        Plan plan = createPlan(planName);
        assertBool(canAccessLibrary, plan.canAccessLibrary(), planName, "canAccessLibrary");
        assertBool(canSkip, plan.canSkip(), planName, "canSkip");
        assertBool(canChooseWhatToPlay, plan.canChooseWhatToPlay(), planName, "canChooseWhatToPlay");
        assertBool(hasAccessToFavorites, plan.hasAccessToFavorites(), planName, "hasAccessToFavorites");
    }


    private static Plan createPlan(String type) {
        switch (type) {
            case "Free":        return new PlanFree();
            case "PremiumBase": return new PlanPremiumBase();
            case "PremiumTop":  return new PlanPremiumTop();
            default: throw new IllegalArgumentException("Unknown plan: " + type);
        }
    }

    private static void assertBool(boolean expected, boolean actual, String plan, String op) {
        if (expected) assertTrue(actual,  op + " should be true for "  + plan);
        else          assertFalse(actual, op + " should be false for " + plan);
    }
}
