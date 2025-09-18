package com.example.basicwindowsapp.dao;

import com.example.basicwindowsapp.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * メッセージDAO（Data Access Object）クラス
 * 
 * <p>メッセージエンティティに対するCRUD操作を提供します。
 * データベースとのやり取りを一元化し、ビジネスロジックから
 * データアクセス詳細を分離します。</p>
 * 
 * @author basic-windows-app
 * @version 0.2.0
 * @since 0.2.0
 */
public class MessageDao {
    
    /**
     * DatabaseManagerのインスタンス
     */
    private final DatabaseManager dbManager;
    
    /**
     * デフォルトメッセージ（削除時の復旧用）
     */
    private static final String DEFAULT_MESSAGE = "Hello World";
    
    /**
     * コンストラクタ
     */
    public MessageDao() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * 新しいメッセージを挿入します
     * 
     * @param message 挿入するメッセージオブジェクト
     * @return 挿入されたメッセージのID
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public int insertMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (text, created_at) VALUES (?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, message.getText());
            pstmt.setLong(2, message.getCreatedAt());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("メッセージの挿入に失敗しました。");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("メッセージの挿入に失敗しました。IDが生成されませんでした。");
                }
            }
        }
    }
    
    /**
     * 全てのメッセージを取得します
     * 
     * @return メッセージのリスト
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT id, text, created_at FROM messages ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("id"),
                    rs.getString("text"),
                    rs.getLong("created_at")
                );
                messages.add(message);
            }
        }
        
        return messages;
    }
    
    /**
     * IDでメッセージを取得します
     * 
     * @param id メッセージID
     * @return メッセージオブジェクト、見つからない場合はnull
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public Message getMessageById(int id) throws SQLException {
        String sql = "SELECT id, text, created_at FROM messages WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getLong("created_at")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * 最新のメッセージを取得します
     * 
     * @return 最新のメッセージ、存在しない場合はnull
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public Message getLatestMessage() throws SQLException {
        String sql = "SELECT id, text, created_at FROM messages ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return new Message(
                    rs.getInt("id"),
                    rs.getString("text"),
                    rs.getLong("created_at")
                );
            }
        }
        
        return null;
    }
    
    /**
     * メッセージを更新します
     * 
     * @param message 更新するメッセージオブジェクト
     * @return 更新された行数
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public int updateMessage(Message message) throws SQLException {
        String sql = "UPDATE messages SET text = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, message.getText());
            pstmt.setInt(2, message.getId());
            
            return pstmt.executeUpdate();
        }
    }
    
    /**
     * メッセージを削除します
     * 
     * @param id 削除するメッセージのID
     * @return 削除された行数
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public int deleteMessage(int id) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int deletedRows = pstmt.executeUpdate();
            
            // 全てのメッセージが削除された場合、デフォルトメッセージを追加
            if (deletedRows > 0 && getAllMessages().isEmpty()) {
                insertDefaultMessage();
            }
            
            return deletedRows;
        }
    }
    
    /**
     * 全てのメッセージを削除します
     * 
     * @return 削除された行数
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public int deleteAllMessages() throws SQLException {
        String sql = "DELETE FROM messages";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int deletedRows = pstmt.executeUpdate();
            
            // 削除後にデフォルトメッセージを追加
            if (deletedRows > 0) {
                insertDefaultMessage();
            }
            
            return deletedRows;
        }
    }
    
    /**
     * デフォルトメッセージを挿入します（削除後の復旧用）
     * 
     * @throws SQLException データベース操作エラーが発生した場合
     */
    private void insertDefaultMessage() throws SQLException {
        Message defaultMessage = new Message(DEFAULT_MESSAGE, System.currentTimeMillis());
        insertMessage(defaultMessage);
        System.out.println("デフォルトメッセージを復旧しました: " + DEFAULT_MESSAGE);
    }
    
    /**
     * メッセージ数を取得します
     * 
     * @return メッセージ数
     * @throws SQLException データベース操作エラーが発生した場合
     */
    public int getMessageCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
}