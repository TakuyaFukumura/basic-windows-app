package com.example.basicwindowsapp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * アプリケーションデータのバックアップと復元を提供します。
 */
public final class BackupService {

    private static final String DATABASE_ENTRY = "basicwindowsapp.db";
    private static final String SETTINGS_ENTRY = "settings.properties";

    private BackupService() {
    }

    /**
     * データベースと設定を1つのZIPファイルへ保存します。
     *
     * @param archive  出力先
     * @param database データベースファイル
     * @param settings 設定ファイル
     * @throws IOException 入出力に失敗した場合
     */
    public static void createBackup(Path archive, Path database, Path settings) throws IOException {
        try (OutputStream output = Files.newOutputStream(archive);
             ZipOutputStream zip = new ZipOutputStream(output)) {
            addFile(zip, DATABASE_ENTRY, database);
            if (Files.exists(settings)) {
                addFile(zip, SETTINGS_ENTRY, settings);
            }
        }
    }

    /**
     * ZIPバックアップからデータベースと設定を復元します。
     *
     * @param archive  入力元
     * @param database 復元先データベース
     * @param settings 復元先設定ファイル
     * @throws IOException 入出力または形式検証に失敗した場合
     */
    public static void restoreBackup(Path archive, Path database, Path settings) throws IOException {
        Path parent = database.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Set<String> restored = new HashSet<>();
        try (InputStream input = Files.newInputStream(archive);
             ZipInputStream zip = new ZipInputStream(input)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                Path destination;
                if (DATABASE_ENTRY.equals(entry.getName())) {
                    destination = database;
                    restored.add(DATABASE_ENTRY);
                } else if (SETTINGS_ENTRY.equals(entry.getName())) {
                    destination = settings;
                    restored.add(SETTINGS_ENTRY);
                } else {
                    throw new IOException("バックアップに未対応のファイルが含まれています。");
                }
                Files.createDirectories(destination.toAbsolutePath().getParent());
                try (OutputStream output = Files.newOutputStream(destination)) {
                    zip.transferTo(output);
                }
            }
        }
        if (!restored.contains(DATABASE_ENTRY)) {
            throw new IOException("バックアップにデータベースが含まれていません。");
        }
    }

    private static void addFile(ZipOutputStream zip, String entryName, Path source) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("バックアップ対象が存在しません: " + source);
        }
        zip.putNextEntry(new ZipEntry(entryName));
        Files.copy(source, zip);
        zip.closeEntry();
    }
}
