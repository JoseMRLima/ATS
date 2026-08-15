package org.Utils;

import org.Model.SpotifUM;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SerializationTest {

    @TempDir
    Path tempDir;

    @Test
    void testExportAndImport() {
        SpotifUM original = new SpotifUM();
        original.addNewUser("alice", "a@b.com", "addr", "pw");

        File file = tempDir.resolve("model.ser").toFile();
        assertDoesNotThrow(() -> Serialization.exportar(original, file.getAbsolutePath()));

        SpotifUM imported = Serialization.importar(file.getAbsolutePath());
        assertTrue(imported.userExists("alice"));
    }

    @Test
    void testExportNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Serialization.exportar(null, tempDir.resolve("x.ser").toString()));
    }

    @Test
    void testExportInvalidPathThrows() {
        SpotifUM model = new SpotifUM();
        assertThrows(RuntimeException.class,
                () -> Serialization.exportar(model, "/nonexistent/path/x.ser"));
    }

    @Test
    void testImportInvalidPathThrows() {
        assertThrows(RuntimeException.class,
                () -> Serialization.importar("/nonexistent/file.ser"));
    }

    @Test
    void testExportAndImportPreservesData() {
        SpotifUM original = new SpotifUM();
        original.addNewAlbum("TestAlbum", "Artist");

        File file = tempDir.resolve("data.ser").toFile();
        Serialization.exportar(original, file.getAbsolutePath());

        SpotifUM loaded = Serialization.importar(file.getAbsolutePath());
        assertTrue(loaded.albumExists("TestAlbum"));
    }

    @Test
    void testConstructorInstantiation() {
        assertNotNull(new Serialization());
    }
}
