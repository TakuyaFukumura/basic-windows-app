# Basic Windows App

基本的なJavaFXを使用したWindowsアプリケーションのテンプレートリポジトリです。

## 概要

このプロジェクトは、JavaFXを使用して「Hello World」と表示するシンプルなGUIアプリケーションです。今後のWindowsアプリ開発のベースとして使用することができます。

## 特徴

- **シンプルで理解しやすい構成**: 初学者でも理解しやすいコード構造
- **拡張性**: 新しい機能を追加しやすい設計
- **詳細な日本語コメント**: 初学者向けのJavadocコメント
- **Maven対応**: 依存関係の管理とビルドが簡単

## 必要な環境

- **Java**: Java 17 以上
- **Maven**: 3.6.0 以上
- **OS**: Windows 10/11（他のOSでも動作可能）

## セットアップ手順

### 1. リポジトリのクローン

```bash
git clone https://github.com/TakuyaFukumura/basic-windows-app.git
cd basic-windows-app
```

### 2. 依存関係のインストール

```bash
mvn clean install
```

## 実行方法

### 方法1: Maven JavaFXプラグインを使用（推奨）

```bash
mvn javafx:run
```

### 方法2: Javaコマンドを直接使用

まずプロジェクトをコンパイルします：

```bash
mvn clean compile
```

次に、JavaFXモジュールを指定してアプリケーションを実行します：

```bash
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.example.basicwindowsapp.BasicWindowsApp
```

**注意**: `path/to/javafx/lib`は、お使いの環境のJavaFXライブラリパスに置き換えてください。

### 方法3: 実行可能JARファイルの作成

```bash
mvn clean package
```

この後、`target`ディレクトリに作成されたJARファイルを実行できます。

## プロジェクト構造

```
basic-windows-app/
├── pom.xml                                    # Mavenビルド設定
├── README.md                                  # このファイル
├── .gitignore                                 # Git無視ファイル設定
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── basicwindowsapp/
        │               └── BasicWindowsApp.java   # メインアプリケーションクラス
        └── resources/                         # リソースファイル用ディレクトリ
```

## 主要なファイルの説明

### BasicWindowsApp.java

メインのアプリケーションクラスです。JavaFXの`Application`クラスを継承し、以下の機能を提供します：

- **ウィンドウの作成**: 400x300ピクセルのメインウィンドウ
- **Hello Worldメッセージの表示**: 中央に配置された大きなテキスト
- **レスポンシブデザイン**: ウィンドウサイズ変更に対応

### pom.xml

Maven設定ファイルです。以下の設定が含まれています：

- **Java 17対応**: 最新のJava機能を使用可能
- **JavaFX依存関係**: JavaFX ControlsとFXMLライブラリ
- **プラグイン設定**: コンパイルと実行用の設定

## 開発ガイド

### 新機能の追加

1. **新しいクラスの追加**: `src/main/java/com/example/basicwindowsapp/`ディレクトリに新しいJavaファイルを作成
2. **リソースファイルの追加**: `src/main/resources/`ディレクトリにFXMLファイルや画像などを配置
3. **依存関係の追加**: 必要に応じて`pom.xml`に新しい依存関係を追加

### コードスタイル

- **日本語コメント**: クラスとメソッドには詳細な日本語のJavadocコメントを記述
- **適切な命名**: クラス名、メソッド名、変数名は英語で明確に記述
- **インデント**: 4スペースを使用

## トラブルシューティング

### JavaFXランタイムが見つからない場合

Java 11以降では、JavaFXはJDKから分離されています。以下の対処法があります：

1. **JavaFX SDKのダウンロード**: [OpenJFX公式サイト](https://openjfx.io/)からダウンロード
2. **環境変数の設定**: `PATH_TO_FX`環境変数にJavaFXライブラリパスを設定
3. **IDEの設定**: IntelliJ IDEAやEclipseでJavaFXライブラリを設定

### ビルドエラーが発生する場合

```bash
mvn clean
mvn compile
```

で依存関係をクリアしてから再ビルドしてください。

## バージョン情報

- **現在のバージョン**: 0.1.0
- **バージョニング方式**: [セマンティックバージョニング](https://semver.org/lang/ja/)

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## 貢献

プルリクエストやイシューの報告を歓迎します。開発に参加される際は、コーディングスタイルと日本語コメントの記述をお守りください。

## 今後の予定

- [ ] FXMLベースのUI設計
- [ ] アプリケーションアイコンの追加
- [ ] 設定ファイルの対応
- [ ] ログ機能の実装
- [ ] 自動更新機能の追加
