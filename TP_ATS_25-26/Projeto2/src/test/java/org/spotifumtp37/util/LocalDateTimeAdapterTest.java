package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Test
    void roundTrip() {
        LocalDateTime original = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
        String json = gson.toJson(original, LocalDateTime.class);
        LocalDateTime restored = gson.fromJson(json, LocalDateTime.class);
        assertEquals(original, restored);
    }

    @Test
    void serializeProducesIsoString() {
        LocalDateTime dt = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        String json = gson.toJson(dt, LocalDateTime.class);
        assertTrue(json.contains("2023-01-01"));
    }

    @Test
    void roundTripWithNanoseconds() {
        LocalDateTime dt = LocalDateTime.of(2025, 12, 31, 23, 59, 59, 123456000);
        String json = gson.toJson(dt, LocalDateTime.class);
        LocalDateTime restored = gson.fromJson(json, LocalDateTime.class);
        assertEquals(dt, restored);
    }
}
