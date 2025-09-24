package com.example.basicwindowsapp;

import com.example.basicwindowsapp.dao.DatabaseManager;
import com.example.basicwindowsapp.dao.MessageDao;
import com.example.basicwindowsapp.model.Message;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 基本的なJavaFXアプリケーションのメインクラス
 *
 * <p>このクラスは、JavaFXとSQLiteを使用してメッセージの表示・編集・削除・登録を行う
 * Windowsアプリケーションを実装しています。</p>
 *
 * <p>JavaFXのApplicationクラスを継承することで、GUI アプリケーションとして
 * 動作します。start()メソッドでウィンドウの構成と表示を行います。</p>
 *
 * <h3>機能：</h3>
 * <ul>
 *   <li>SQLiteデータベースからメッセージを取得・表示</li>
 *   <li>メッセージの新規作成・編集・削除</li>
 *   <li>メッセージ一覧をTableViewで表示</li>
 *   <li>削除時のデフォルトメッセージ復旧機能</li>
 * </ul>
 *
 * <h3>使用方法：</h3>
 * <ul>
 *   <li>Maven: {@code mvn javafx:run}</li>
 *   <li>Java: {@code java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml com.example.basicwindowsapp.BasicWindowsApp}</li>
 * </ul>
 *
 * @author basic-windows-app
 * @version 0.2.0
 * @since 0.1.0
 */
public class BasicWindowsApp extends Application {
    
    /**
     * メッセージDAO
     */
    private MessageDao messageDao;
    
    /**
     * メインメッセージ表示ラベル
     */
    private Label mainMessageLabel;
    
    /**
     * メッセージ一覧テーブル
     */
    private TableView<Message> messageTable;
    
    /**
     * メッセージ一覧データ
     */
    private ObservableList<Message> messageData;

    /**
     * アプリケーションのメインメソッド
     *
     * <p>Javaアプリケーションの標準的なエントリーポイントです。
     * JavaFXアプリケーションを起動するために、Application.launch()メソッドを
     * 呼び出します。</p>
     *
     * <p>launch()メソッドは内部的に以下の処理を行います：</p>
     * <ul>
     *   <li>JavaFXランタイムの初期化</li>
     *   <li>アプリケーションインスタンスの作成</li>
     *   <li>start()メソッドの呼び出し</li>
     * </ul>
     *
     * @param args コマンドライン引数。現在のバージョンでは使用していません。
     */
    public static void main(String[] args) {
        // JavaFXアプリケーションを起動
        launch(args);
    }

    /**
     * JavaFXアプリケーションのエントリーポイント
     *
     * <p>このメソッドはJavaFXランタイムによって呼び出され、アプリケーションの
     * ユーザーインターフェースを構築します。SQLiteデータベースを初期化し、
     * メッセージの表示・編集機能を提供するUIを作成します。</p>
     *
     * <p>処理の流れ：</p>
     * <ol>
     *   <li>データベースの初期化</li>
     *   <li>メインメッセージラベルの作成</li>
     *   <li>メッセージ一覧テーブルの作成</li>
     *   <li>CRUD操作ボタンの作成</li>
     *   <li>レイアウトの構成</li>
     *   <li>ステージ（ウィンドウ）の設定と表示</li>
     * </ol>
     *
     * @param primaryStage メインウィンドウを表すStageオブジェクト。
     *                     JavaFXランタイムによって自動的に作成され、渡されます。
     * @throws Exception アプリケーション起動時にエラーが発生した場合
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // データベースの初期化
        initializeDatabase();
        
        // UIコンポーネントの初期化
        initializeUI();
        
        // メインレイアウトの作成
        BorderPane root = createMainLayout();
        
        // シーンの作成
        Scene scene = new Scene(root, 800, 600);
        
        // ステージ（ウィンドウ）の設定
        primaryStage.setTitle("Basic Windows App - Message Manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        
        // ウィンドウを画面に表示
        primaryStage.show();
        
        // 初期データの読み込み
        refreshMessageDisplay();
        refreshMessageTable();
    }
    
    /**
     * データベースを初期化します
     * 
     * @throws SQLException データベース初期化エラーが発生した場合
     */
    private void initializeDatabase() throws SQLException {
        DatabaseManager.getInstance().initializeDatabase();
        messageDao = new MessageDao();
        System.out.println("データベースが初期化されました。");
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeUI() {
        // メインメッセージラベルの初期化
        mainMessageLabel = new Label();
        mainMessageLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // メッセージデータの初期化
        messageData = FXCollections.observableArrayList();
        
        // メッセージテーブルの初期化
        messageTable = createMessageTable();
    }
    
    /**
     * メインレイアウトを作成します
     * 
     * @return メインレイアウト
     */
    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        
        // 上部：メインメッセージ表示エリア
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // 中央：メッセージ一覧テーブル
        VBox centerSection = createCenterSection();
        root.setCenter(centerSection);
        
        // 下部：操作ボタン
        HBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
        
        return root;
    }
    
    /**
     * 上部セクション（メインメッセージ表示）を作成します
     * 
     * @return 上部セクション
     */
    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(Pos.CENTER);
        topSection.setStyle("-fx-background-color: #f0f0f0;");
        
        Label titleLabel = new Label("現在のメッセージ");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        topSection.getChildren().addAll(titleLabel, mainMessageLabel);
        
        return topSection;
    }
    
    /**
     * 中央セクション（メッセージ一覧テーブル）を作成します
     * 
     * @return 中央セクション
     */
    private VBox createCenterSection() {
        VBox centerSection = new VBox(10);
        centerSection.setPadding(new Insets(20));
        
        Label tableLabel = new Label("メッセージ一覧");
        tableLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        centerSection.getChildren().addAll(tableLabel, messageTable);
        
        return centerSection;
    }
    
    /**
     * 下部セクション（操作ボタン）を作成します
     * 
     * @return 下部セクション
     */
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(10);
        bottomSection.setPadding(new Insets(20));
        bottomSection.setAlignment(Pos.CENTER);
        
        // ボタンの作成
        Button addButton = new Button("新規作成");
        Button editButton = new Button("編集");
        Button deleteButton = new Button("削除");
        Button refreshButton = new Button("更新");
        
        // ボタンイベントの設定
        addButton.setOnAction(e -> showAddMessageDialog());
        editButton.setOnAction(e -> showEditMessageDialog());
        deleteButton.setOnAction(e -> deleteSelectedMessage());
        refreshButton.setOnAction(e -> {
            refreshMessageDisplay();
            refreshMessageTable();
        });
        
        bottomSection.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        return bottomSection;
    }
    
    /**
     * メッセージテーブルを作成します
     * 
     * @return メッセージテーブル
     */
    private TableView<Message> createMessageTable() {
        TableView<Message> table = new TableView<>();
        table.setItems(messageData);
        
        // ID列
        TableColumn<Message, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        // メッセージ列
        TableColumn<Message, String> textCol = new TableColumn<>("メッセージ");
        textCol.setCellValueFactory(new PropertyValueFactory<>("text"));
        textCol.setPrefWidth(400);
        
        // 作成日時列
        TableColumn<Message, String> dateCol = new TableColumn<>("作成日時");
        dateCol.setCellValueFactory(cellData -> {
            long timestamp = cellData.getValue().getCreatedAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return new javafx.beans.property.SimpleStringProperty(sdf.format(new Date(timestamp)));
        });
        dateCol.setPrefWidth(150);
        
        table.getColumns().addAll(idCol, textCol, dateCol);
        
        return table;
    }
    
    /**
     * メインメッセージ表示を更新します
     */
    private void refreshMessageDisplay() {
        try {
            Message latestMessage = messageDao.getLatestMessage();
            if (latestMessage != null) {
                mainMessageLabel.setText(latestMessage.getText());
            } else {
                mainMessageLabel.setText("メッセージがありません");
            }
        } catch (SQLException e) {
            showErrorDialog("メッセージ取得エラー", "メッセージの取得に失敗しました: " + e.getMessage());
        }
    }
    
    /**
     * メッセージテーブルを更新します
     */
    private void refreshMessageTable() {
        try {
            List<Message> messages = messageDao.getAllMessages();
            messageData.clear();
            messageData.addAll(messages);
        } catch (SQLException e) {
            showErrorDialog("データ取得エラー", "メッセージ一覧の取得に失敗しました: " + e.getMessage());
        }
    }
    
    /**
     * 新規メッセージ作成ダイアログを表示します
     */
    private void showAddMessageDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("新規メッセージ作成");
        dialog.setHeaderText("新しいメッセージを入力してください");
        dialog.setContentText("メッセージ:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            if (!text.trim().isEmpty()) {
                try {
                    Message newMessage = new Message(text.trim(), System.currentTimeMillis());
                    messageDao.insertMessage(newMessage);
                    refreshMessageDisplay();
                    refreshMessageTable();
                    showInfoDialog("成功", "メッセージが追加されました。");
                } catch (SQLException e) {
                    showErrorDialog("追加エラー", "メッセージの追加に失敗しました: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * メッセージ編集ダイアログを表示します
     */
    private void showEditMessageDialog() {
        Message selectedMessage = messageTable.getSelectionModel().getSelectedItem();
        if (selectedMessage == null) {
            showWarningDialog("選択エラー", "編集するメッセージを選択してください。");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog(selectedMessage.getText());
        dialog.setTitle("メッセージ編集");
        dialog.setHeaderText("メッセージを編集してください");
        dialog.setContentText("メッセージ:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            if (!text.trim().isEmpty()) {
                try {
                    selectedMessage.setText(text.trim());
                    messageDao.updateMessage(selectedMessage);
                    refreshMessageDisplay();
                    refreshMessageTable();
                    showInfoDialog("成功", "メッセージが更新されました。");
                } catch (SQLException e) {
                    showErrorDialog("更新エラー", "メッセージの更新に失敗しました: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * 選択されたメッセージを削除します
     */
    private void deleteSelectedMessage() {
        Message selectedMessage = messageTable.getSelectionModel().getSelectedItem();
        if (selectedMessage == null) {
            showWarningDialog("選択エラー", "削除するメッセージを選択してください。");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("削除確認");
        confirmDialog.setHeaderText("メッセージを削除しますか？");
        confirmDialog.setContentText("メッセージ: \"" + selectedMessage.getText() + "\"");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                messageDao.deleteMessage(selectedMessage.getId());
                refreshMessageDisplay();
                refreshMessageTable();
                showInfoDialog("成功", "メッセージが削除されました。");
            } catch (SQLException e) {
                showErrorDialog("削除エラー", "メッセージの削除に失敗しました: " + e.getMessage());
            }
        }
    }
    
    /**
     * 情報ダイアログを表示します
     * 
     * @param title タイトル
     * @param message メッセージ
     */
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 警告ダイアログを表示します
     * 
     * @param title タイトル
     * @param message メッセージ
     */
    private void showWarningDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * エラーダイアログを表示します
     * 
     * @param title タイトル
     * @param message メッセージ
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
