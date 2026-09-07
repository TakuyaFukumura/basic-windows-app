package com.example.basicwindowsapp.io;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BackupServiceTest {

    @Test
    void backupRoundTripRestoresDatabaseAndSettings() throws Exception {
        Path directory = Files.createTempDirectory("basic-windows-backup");
        try {
            Path database = directory.resolve("source.db");
            Path settings = directory.resolve("source.properties");
            Path archive = directory.resolve("backup.bwa");
            Path restoredDatabase = directory.resolve("restored.db");
            Path restoredSettings = directory.resolve("restored.properties");
            Files.writeString(database, "database");
            Files.writeString(settings, "darkMode=true");

            BackupService.createBackup(archive, database, settings);
            BackupService.restoreBackup(archive, restoredDatabase, restoredSettings);

            assertEquals("database", Files.readString(restoredDatabase));
            assertEquals("darkMode=true", Files.readString(restoredSettings));
        } finally {
            try (var files = Files.walk(directory)) {
                for (Path path : files.sorted((left, right) -> right.compareTo(left)).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }
}
