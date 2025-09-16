package com.example.basicwindowsapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 基本的なJavaFXアプリケーションのメインクラス
 *
 * <p>このクラスは、JavaFXを使用して「Hello World」メッセージを表示する
 * シンプルなWindowsアプリケーションを実装しています。</p>
 *
 * <p>JavaFXのApplicationクラスを継承することで、GUI アプリケーションとして
 * 動作します。start()メソッドでウィンドウの構成と表示を行います。</p>
 *
 * <h3>使用方法：</h3>
 * <ul>
 *   <li>Maven: {@code mvn javafx:run}</li>
 *   <li>Java: {@code java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml com.example.basicwindowsapp.BasicWindowsApp}</li>
 * </ul>
 *
 * @author basic-windows-app
 * @version 0.1.0
 * @since 0.1.0
 */
public class BasicWindowsApp extends Application {

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
     * ユーザーインターフェースを構築します。ウィンドウ（Stage）とシーン（Scene）を
     * 作成し、「Hello World」メッセージを表示します。</p>
     *
     * <p>処理の流れ：</p>
     * <ol>
     *   <li>ラベルコンポーネントの作成</li>
     *   <li>レイアウトコンテナの作成と設定</li>
     *   <li>シーンの作成</li>
     *   <li>ステージ（ウィンドウ）の設定と表示</li>
     * </ol>
     *
     * @param primaryStage メインウィンドウを表すStageオブジェクト。
     *                     JavaFXランタイムによって自動的に作成され、渡されます。
     * @throws Exception アプリケーション起動時にエラーが発生した場合
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // ラベルコンポーネントの作成
        // Hello Worldメッセージを表示するためのテキストラベル
        Label helloLabel = new Label("Hello World");

        // フォントサイズを大きくして見やすくする
        helloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // レイアウトコンテナの作成
        // StackPaneは子要素を中央に配置するレイアウトコンテナ
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(helloLabel);

        // シーンの作成
        // ウィンドウに表示するコンテンツを含むシーンを作成
        // 幅400ピクセル、高さ300ピクセルで初期化
        Scene scene = new Scene(root, 400, 300);

        // ステージ（ウィンドウ）の設定
        primaryStage.setTitle("Basic Windows App - Hello World");
        primaryStage.setScene(scene);

        // ウィンドウサイズの変更を制限（オプション）
        primaryStage.setResizable(true);

        // ウィンドウの最小サイズを設定
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(200);

        // ウィンドウを画面に表示
        primaryStage.show();
    }
}
