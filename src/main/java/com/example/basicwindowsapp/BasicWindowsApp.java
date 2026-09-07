package com.example.basicwindowsapp;

import com.example.basicwindowsapp.dao.DatabaseManager;
import com.example.basicwindowsapp.dao.MessageDao;
import com.example.basicwindowsapp.config.ApplicationSettings;
import com.example.basicwindowsapp.io.MessageFileService;
import com.example.basicwindowsapp.model.Message;
import com.example.basicwindowsapp.validation.MessageValidator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 *   <li>メッセージ管理画面とアプリ情報画面をTabPaneで切替</li>
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
 * @version 0.4.0
 * @since 0.1.0
 */
public class BasicWindowsApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(BasicWindowsApp.class.getName());
    private ApplicationSettings settings;

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

    private FilteredList<Message> filteredMessageData;

    private TextField searchField;

    /**
     * ダークモードが有効かどうか
     */
    private boolean darkMode;

    private boolean ioTaskRunning;

    private Label statusLabel;

    private ProgressIndicator progressIndicator;

    /**
     * アプリケーション共通のスタイルシート
     */
    private static final String STYLESHEET = BasicWindowsApp.class
            .getResource("/styles.css").toExternalForm();

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
        settings = ApplicationSettings.load();
        darkMode = settings.isDarkMode();
        // データベースの初期化
        initializeDatabase();
        
        // UIコンポーネントの初期化
        initializeUI();
        
        // メインレイアウトの作成
        BorderPane root = createMainLayout();
        applyTheme(root);
        
        // シーンの作成
        Scene scene = new Scene(root, settings.getWindowWidth(), settings.getWindowHeight());
        scene.getStylesheets().add(STYLESHEET);
        
        // ステージ（ウィンドウ）の設定
        primaryStage.setTitle("Basic Windows App - Message Manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        if (Double.isFinite(settings.getWindowX())) {
            primaryStage.setX(settings.getWindowX());
        }
        if (Double.isFinite(settings.getWindowY())) {
            primaryStage.setY(settings.getWindowY());
        }
        primaryStage.setOnCloseRequest(event -> saveSettings(primaryStage));
        
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
        LOGGER.info("データベースが初期化されました。");
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeUI() {
        // メインメッセージラベルの初期化
        mainMessageLabel = new Label();
        mainMessageLabel.getStyleClass().add("message-label");
        
        // メッセージデータの初期化
        messageData = FXCollections.observableArrayList();
        filteredMessageData = new FilteredList<>(messageData);
        
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
        root.getStyleClass().add("app-root");
        root.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles() && !ioTaskRunning) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            event.consume();
        });
        root.setOnDragDropped(event -> {
            if (!ioTaskRunning && event.getDragboard().hasFiles()) {
                handleDroppedFiles(event.getDragboard().getFiles());
            }
            event.setDropCompleted(true);
            event.consume();
        });
        
        MenuBar menuBar = createMenuBar();

        root.setTop(menuBar);
        root.setCenter(createTabPane(root));
        
        return root;
    }

    private TabPane createTabPane(BorderPane root) {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("app-tab-pane");
        Tab messagesTab = new Tab("メッセージ");
        messagesTab.setClosable(false);
        messagesTab.setContent(createMessageLayout(root));

        Tab aboutTab = new Tab("アプリ情報");
        aboutTab.setClosable(false);
        aboutTab.setContent(createAboutLayout());

        tabPane.getTabs().addAll(messagesTab, aboutTab);
        return tabPane;
    }

    private BorderPane createMessageLayout(BorderPane root) {
        BorderPane messageLayout = new BorderPane();
        messageLayout.setTop(createTopSection(root));
        messageLayout.setCenter(createCenterSection());
        messageLayout.setBottom(createBottomSection());
        return messageLayout;
    }

    private VBox createAboutLayout() {
        VBox aboutLayout = new VBox(12);
        aboutLayout.setPadding(new Insets(24));
        aboutLayout.getStyleClass().add("center-section");

        Label title = new Label("Basic Windows App");
        title.getStyleClass().add("section-title");
        Label description = new Label(
                "JavaFX と SQLite を使用したメッセージ管理アプリケーションです。");
        description.setWrapText(true);
        Label storage = new Label(
                "データ保存先: ユーザーホーム/.basic-windows-app");
        storage.setWrapText(true);
        aboutLayout.getChildren().addAll(title, description, storage);
        return aboutLayout;
    }
    
    /**
     * 上部セクション（メインメッセージ表示）を作成します
     * 
     * @return 上部セクション
     */
    private VBox createTopSection(BorderPane root) {
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(Pos.CENTER);
        topSection.getStyleClass().add("top-section");
        
        Label titleLabel = new Label("現在のメッセージ");
        titleLabel.getStyleClass().add("section-title");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        ToggleButton themeToggle = new ToggleButton(darkMode ? "🌙" : "☀");
        themeToggle.setAccessibleText("テーマ切替");
        Tooltip themeTooltip = new Tooltip(darkMode ? "ライトモードに切替" : "ダークモードに切替");
        themeTooltip.getStyleClass().add("app-tooltip");
        if (darkMode) {
            themeTooltip.getStyleClass().add("dark-mode");
        }
        themeToggle.setTooltip(themeTooltip);
        themeToggle.setSelected(darkMode);
        themeToggle.setOnAction(e -> {
            darkMode = themeToggle.isSelected();
            settings.setDarkMode(darkMode);
            saveSettings((Stage) themeToggle.getScene().getWindow());
            themeToggle.setText(darkMode ? "🌙" : "☀");
            themeTooltip.setText(darkMode ? "ライトモードに切替" : "ダークモードに切替");
            if (darkMode) {
                if (!themeTooltip.getStyleClass().contains("dark-mode")) {
                    themeTooltip.getStyleClass().add("dark-mode");
                }
            } else {
                themeTooltip.getStyleClass().remove("dark-mode");
            }
            applyTheme(root);
        });

        HBox header = new HBox(10, titleLabel, headerSpacer, themeToggle);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("top-header");
        
        topSection.getChildren().addAll(header, mainMessageLabel);
        
        return topSection;
    }

    private void saveSettings(Stage stage) {
        settings.setDarkMode(darkMode);
        settings.setWindowWidth(stage.getWidth());
        settings.setWindowHeight(stage.getHeight());
        settings.setWindowX(stage.getX());
        settings.setWindowY(stage.getY());
        try {
            settings.save();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "アプリケーション設定の保存に失敗しました。", e);
        }
    }
    
    /**
     * 中央セクション（メッセージ一覧テーブル）を作成します
     * 
     * @return 中央セクション
     */
    private VBox createCenterSection() {
        VBox centerSection = new VBox(10);
        centerSection.setPadding(new Insets(20));
        centerSection.getStyleClass().add("center-section");
        
        Label tableLabel = new Label("メッセージ一覧");
        tableLabel.getStyleClass().add("section-title");

        searchField = new TextField();
        searchField.setPromptText("メッセージを検索");
        searchField.setAccessibleText("メッセージ検索");
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredMessageData.setPredicate(message -> newValue == null
                        || newValue.isBlank()
                        || message.getText().toLowerCase(Locale.ROOT)
                                .contains(newValue.trim().toLowerCase(Locale.ROOT))));
        
        centerSection.getChildren().addAll(tableLabel, searchField, messageTable);
        
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
        bottomSection.getStyleClass().add("bottom-section");
        
        // ボタンの作成
        Button addButton = new Button("新規作成");
        Button editButton = new Button("編集");
        Button deleteButton = new Button("削除");
        Button refreshButton = new Button("更新");

        statusLabel = new Label("準備完了");
        statusLabel.getStyleClass().add("status-label");
        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(18, 18);
        progressIndicator.setVisible(false);
        Region statusSpacer = new Region();
        HBox.setHgrow(statusSpacer, Priority.ALWAYS);
        
        // ボタンイベントの設定
        addButton.setOnAction(e -> showAddMessageDialog());
        editButton.setOnAction(e -> showEditMessageDialog());
        deleteButton.setOnAction(e -> deleteSelectedMessage());
        refreshButton.setOnAction(e -> {
            refreshMessageDisplay();
            refreshMessageTable();
        });
        
        bottomSection.getChildren().addAll(
                addButton, editButton, deleteButton, refreshButton,
                statusSpacer, progressIndicator, statusLabel);
        
        return bottomSection;
    }

    private MenuBar createMenuBar() {
        Menu messageMenu = new Menu("メッセージ");
        MenuItem addItem = createMenuItem("新規作成", new KeyCodeCombination(KeyCode.N,
                KeyCombination.CONTROL_DOWN), this::showAddMessageDialog);
        MenuItem editItem = createMenuItem("編集", new KeyCodeCombination(KeyCode.E,
                KeyCombination.CONTROL_DOWN), this::showEditMessageDialog);
        MenuItem deleteItem = createMenuItem("削除", new KeyCodeCombination(KeyCode.DELETE),
                this::deleteSelectedMessage);
        MenuItem refreshItem = createMenuItem("更新", new KeyCodeCombination(KeyCode.F5),
                () -> {
                    refreshMessageDisplay();
                    refreshMessageTable();
                });
        messageMenu.getItems().addAll(addItem, editItem, deleteItem, new SeparatorMenuItem(),
                refreshItem);

        Menu fileMenu = new Menu("ファイル");
        fileMenu.getItems().addAll(
                createMenuItem("インポート", new KeyCodeCombination(KeyCode.I,
                        KeyCombination.CONTROL_DOWN), this::importMessages),
                createMenuItem("エクスポート", new KeyCodeCombination(KeyCode.E,
                        KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), this::exportMessages),
                new SeparatorMenuItem(),
                createMenuItem("終了", new KeyCodeCombination(KeyCode.Q,
                        KeyCombination.CONTROL_DOWN), () -> {
                    Stage stage = (Stage) messageTable.getScene().getWindow();
                    stage.close();
                }));

        Menu viewMenu = new Menu("表示");
        viewMenu.getItems().add(createMenuItem("検索へ移動", new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN), () -> {
            searchField.requestFocus();
            searchField.selectAll();
        }));

        return new MenuBar(fileMenu, messageMenu, viewMenu);
    }

    private MenuItem createMenuItem(String text, KeyCombination accelerator, Runnable action) {
        MenuItem item = new MenuItem(text);
        item.setAccelerator(accelerator);
        item.setOnAction(event -> action.run());
        return item;
    }

    private void importMessages() {
        FileChooser chooser = createMessageFileChooser("メッセージをインポート");
        java.io.File file = chooser.showOpenDialog(messageTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        importMessages(file.toPath());
    }

    private void handleDroppedFiles(List<java.io.File> files) {
        if (files.size() != 1) {
            showWarningDialog("インポートエラー", "一度に取り込めるファイルは1つだけです。");
            return;
        }
        Path path = files.get(0).toPath();
        if (!isSupportedFile(path)) {
            showWarningDialog("インポートエラー", "CSVまたはテキストファイルを指定してください。");
            return;
        }
        importMessages(path);
    }

    private void importMessages(Path path) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws IOException, SQLException {
                MessageFileService.Format format = getFileFormat(path);
                List<Message> messages = MessageFileService.read(path, format);
                for (Message message : messages) {
                    messageDao.insertMessage(message);
                }
                return messages.size();
            }
        };
        executeIoTask(task, count -> {
            refreshMessageDisplay();
            refreshMessageTable();
            showInfoDialog("成功", count + "件のメッセージを取り込みました。");
        }, "インポート");
    }

    private void exportMessages() {
        FileChooser chooser = createMessageFileChooser("メッセージをエクスポート");
        chooser.setInitialFileName("messages.csv");
        java.io.File file = chooser.showSaveDialog(messageTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        if (file.exists() && !confirmOverwrite(file.toPath())) {
            return;
        }

        List<Message> messages = new ArrayList<>(messageData);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                MessageFileService.write(file.toPath(), messages, getFileFormat(file.toPath()));
                return null;
            }
        };
        executeIoTask(task, ignored -> showInfoDialog("成功", "メッセージをエクスポートしました。"), "エクスポート");
    }

    private FileChooser createMessageFileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSVファイル (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("テキストファイル (*.txt)", "*.txt"));
        return chooser;
    }

    private MessageFileService.Format getFileFormat(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".txt")
                ? MessageFileService.Format.TEXT
                : MessageFileService.Format.CSV;
    }

    private boolean isSupportedFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".csv") || fileName.endsWith(".txt");
    }

    private boolean confirmOverwrite(Path path) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("上書き確認");
        dialog.setHeaderText("ファイルは既に存在します。");
        dialog.setContentText(path.getFileName() + "を上書きしますか？");
        styleDialog(dialog);
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private <T> void executeIoTask(Task<T> task, java.util.function.Consumer<T> onSucceeded,
                                   String operation) {
        if (ioTaskRunning) {
            showWarningDialog("処理中", "別のファイル処理が完了するまでお待ちください。");
            return;
        }
        ioTaskRunning = true;
        statusLabel.setText(operation + "中...");
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.setVisible(true);
        task.setOnSucceeded(event -> {
            ioTaskRunning = false;
            progressIndicator.progressProperty().unbind();
            progressIndicator.setVisible(false);
            statusLabel.setText(operation + "完了");
            onSucceeded.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            ioTaskRunning = false;
            progressIndicator.progressProperty().unbind();
            progressIndicator.setVisible(false);
            statusLabel.setText(operation + "失敗");
            Throwable error = task.getException();
            LOGGER.log(Level.WARNING, "メッセージの" + operation + "に失敗しました。", error);
            showErrorDialog(operation + "エラー", "メッセージの" + operation + "に失敗しました: "
                    + error.getMessage());
        });
        Thread thread = new Thread(task, "message-" + operation);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * メッセージテーブルを作成します
     * 
     * @return メッセージテーブル
     */
    private TableView<Message> createMessageTable() {
        TableView<Message> table = new TableView<>();
        table.setItems(filteredMessageData);
        
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
        table.getStyleClass().add("message-table");
        
        return table;
    }

    /**
     * 現在のテーマをメイン画面へ適用します。
     *
     * @param root メインレイアウト
     */
    private void applyTheme(BorderPane root) {
        if (darkMode) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
        } else {
            root.getStyleClass().remove("dark-mode");
        }
    }

    /**
     * ダイアログへアプリケーションのテーマを適用します。
     *
     * @param dialog 対象ダイアログ
     */
    private void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(STYLESHEET);
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        if (darkMode) {
            dialog.getDialogPane().getStyleClass().add("dark-mode");
        }
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
            LOGGER.log(Level.WARNING, "メッセージの取得に失敗しました。", e);
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
            LOGGER.log(Level.WARNING, "メッセージ一覧の取得に失敗しました。", e);
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
        styleDialog(dialog);
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            try {
                String normalizedText = MessageValidator.normalize(text);
                Message newMessage = new Message(normalizedText, System.currentTimeMillis());
                messageDao.insertMessage(newMessage);
                refreshMessageDisplay();
                refreshMessageTable();
                showInfoDialog("成功", "メッセージが追加されました。");
            } catch (IllegalArgumentException e) {
                showWarningDialog("入力エラー", e.getMessage());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "メッセージの追加に失敗しました。", e);
                showErrorDialog("追加エラー", "メッセージの追加に失敗しました: " + e.getMessage());
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
        styleDialog(dialog);
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(text -> {
            try {
                selectedMessage.setText(MessageValidator.normalize(text));
                messageDao.updateMessage(selectedMessage);
                refreshMessageDisplay();
                refreshMessageTable();
                showInfoDialog("成功", "メッセージが更新されました。");
            } catch (IllegalArgumentException e) {
                showWarningDialog("入力エラー", e.getMessage());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "メッセージの更新に失敗しました。", e);
                showErrorDialog("更新エラー", "メッセージの更新に失敗しました: " + e.getMessage());
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
        styleDialog(confirmDialog);
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                messageDao.deleteMessage(selectedMessage.getId());
                refreshMessageDisplay();
                refreshMessageTable();
                showInfoDialog("成功", "メッセージが削除されました。");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "メッセージの削除に失敗しました。", e);
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
        styleDialog(alert);
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
        styleDialog(alert);
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
        styleDialog(alert);
        alert.showAndWait();
    }
}
