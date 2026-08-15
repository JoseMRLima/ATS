package org.spotifumtp37.model.subscription;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PremiumTopTest {

    private final PremiumTop plan = new PremiumTop();

    @Test
    void addPointsApplies2Point5Percent() {
        assertEquals(102.5, plan.addPoints(100), 0.001);
    }

    @Test
    void addPointsFromZeroStaysZero() {
        assertEquals(0.0, plan.addPoints(0), 0.001);
    }

    @Test
    void addPointsExactMultiplier() {
        assertEquals(1.025 * 200, plan.addPoints(200), 0.001);
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
    void canAccessFavouritesList() {
        assertTrue(plan.canAccessFavouritesList());
    }

    @Test
    void givesMorePointsThanPremiumBaseForLargePoints() {
        assertTrue(plan.addPoints(1000) > new PremiumBase().addPoints(1000));
    }

    @Test
    void givesLessPointsThanPremiumBaseForZeroPoints() {
        assertTrue(new PremiumBase().addPoints(0) > plan.addPoints(0));
    }
}
