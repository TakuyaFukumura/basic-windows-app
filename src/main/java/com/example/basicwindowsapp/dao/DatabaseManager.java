package com.example.basicwindowsapp.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * データベース接続・管理クラス
 * 
 * <p>SQLiteデータベースへの接続とテーブルの初期化を管理します。
 * シングルトンパターンを使用してアプリケーション全体で共有されます。</p>
 * 
 * @author basic-windows-app
 * @version 0.2.0
 * @since 0.2.0
 */
public class DatabaseManager {
    
    /**
     * データベースファイル名
     */
    private static final String DB_NAME = "basicwindowsapp.db";
    
    /**
     * データベースURL
     */
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    
    /**
     * シングルトンインスタンス
     */
    private static DatabaseManager instance;
    
    /**
     * プライベートコンストラクタ（シングルトンパターン）
     */
    private DatabaseManager() {
    }
    
    /**
     * DatabaseManagerのインスタンスを取得します（シングルトンパターン）
     * 
     * @return DatabaseManagerのインスタンス
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * データベース接続を取得します
     * 
     * @return データベース接続
     * @throws SQLException データベース接続エラーが発生した場合
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * データベースとテーブルを初期化します
     * 
     * <p>アプリケーション起動時に呼び出され、必要なテーブルを作成し、
     * デフォルトデータを挿入します。</p>
     * 
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // messagesテーブルを作成（存在しない場合のみ）
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    text TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
                """;
            stmt.execute(createTableSQL);
            
            // デフォルトメッセージが存在するかチェック
            String checkDataSQL = "SELECT COUNT(*) FROM messages";
            var resultSet = stmt.executeQuery(checkDataSQL);
            
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                // デフォルトメッセージを挿入
                insertDefaultMessage(conn);
            }
        }
    }
    
    /**
     * デフォルトメッセージをデータベースに挿入します
     * 
     * @param conn データベース接続
     * @throws SQLException データベース操作エラーが発生した場合
     */
    private void insertDefaultMessage(Connection conn) throws SQLException {
        long currentTime = System.currentTimeMillis();
        String insertSQL = "INSERT INTO messages (text, created_at) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Hello World");
            pstmt.setLong(2, currentTime);
            pstmt.executeUpdate();
        }
        
        System.out.println("デフォルトメッセージを挿入しました: Hello World");
    }
    
    /**
     * データベースファイルが存在するかチェックします
     * 
     * @return データベースファイルが存在する場合true
     */
    public boolean databaseExists() {
        File dbFile = new File(DB_NAME);
        return dbFile.exists();
    }
    
    /**
     * データベースファイルを削除します（テスト・開発用）
     * 
     * @return 削除に成功した場合true
     */
    public boolean deleteDatabase() {
        File dbFile = new File(DB_NAME);
        if (dbFile.exists()) {
            return dbFile.delete();
        }
        return true;
    }
}
