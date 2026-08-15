package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.subscription.SubscriptionPlan;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionPlanAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SubscriptionPlan.class, new SubscriptionPlanAdapter())
            .create();

    @Test
    void freePlanRoundTrip() {
        SubscriptionPlan plan = new FreePlan();
        String json = gson.toJson(plan, SubscriptionPlan.class);
        SubscriptionPlan restored = gson.fromJson(json, SubscriptionPlan.class);
        assertInstanceOf(FreePlan.class, restored);
    }

    @Test
    void premiumBaseRoundTrip() {
        SubscriptionPlan plan = new PremiumBase();
        String json = gson.toJson(plan, SubscriptionPlan.class);
        SubscriptionPlan restored = gson.fromJson(json, SubscriptionPlan.class);
        assertInstanceOf(PremiumBase.class, restored);
    }

    @Test
    void premiumTopRoundTrip() {
        SubscriptionPlan plan = new PremiumTop();
        String json = gson.toJson(plan, SubscriptionPlan.class);
        SubscriptionPlan restored = gson.fromJson(json, SubscriptionPlan.class);
        assertInstanceOf(PremiumTop.class, restored);
    }

    @Test
    void missingTypeDefaultsToFreePlan() {
        String json = "{}";
        SubscriptionPlan restored = gson.fromJson(json, SubscriptionPlan.class);
        assertInstanceOf(FreePlan.class, restored);
    }

    @Test
    void serializedJsonContainsTypeField() {
        String json = gson.toJson(new PremiumTop(), SubscriptionPlan.class);
        assertTrue(json.contains("PremiumTop"));
    }
}
