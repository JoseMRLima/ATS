package org.spotifumtp37.model.subscription;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PremiumBaseTest {

    private final PremiumBase plan = new PremiumBase();

    @Test
    void addPointsFromZeroGivesTen() {
        assertEquals(10.0, plan.addPoints(0), 0.001);
    }

    @Test
    void addPointsAccumulates() {
        assertEquals(60.0, plan.addPoints(50), 0.001);
    }

    @Test
    void canCreatePlaylist() {
        assertTrue(plan.canCreatePlaylist());
    }

    @Test
    void canBrowsePlaylist() {
        assertTrue(plan.canBrowsePlaylist());
    }

    @Test
    void cannotAccessFavouritesList() {
        assertFalse(plan.canAccessFavouritesList());
    }

    @Test
    void givesMorePointsThanFreePlan() {
        assertTrue(plan.addPoints(0) > new FreePlan().addPoints(0));
    }
}
