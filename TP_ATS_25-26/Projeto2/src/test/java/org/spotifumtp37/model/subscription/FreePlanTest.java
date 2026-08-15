package org.spotifumtp37.model.subscription;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FreePlanTest {

    private final FreePlan plan = new FreePlan();

    @Test
    void addPointsFromZeroGivesFive() {
        assertEquals(5.0, plan.addPoints(0), 0.001);
    }

    @Test
    void addPointsAccumulates() {
        assertEquals(15.0, plan.addPoints(10), 0.001);
    }

    @Test
    void cannotCreatePlaylist() {
        assertFalse(plan.canCreatePlaylist());
    }

    @Test
    void cannotBrowsePlaylist() {
        assertFalse(plan.canBrowsePlaylist());
    }

    @Test
    void cannotAccessFavouritesList() {
        assertFalse(plan.canAccessFavouritesList());
    }
}
