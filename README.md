# Basic Windows App

JavaFXとSQLiteを使用したメッセージ管理アプリケーションのテンプレートリポジトリです。

## 概要

このプロジェクトは、JavaFXとSQLiteデータベースを使用してメッセージの表示・編集・削除・登録を行うGUIアプリケーションです。
Windowsアプリ開発における基本的なCRUD操作のベースとして使用することができます。

## 機能

- **メッセージ表示**: SQLiteデータベースから取得したメッセージを画面に表示
- **CRUD操作**: メッセージの新規作成・編集・削除・一覧表示
- **メッセージ管理**: TableViewを使用したメッセージ一覧と操作UI
- **デフォルト復旧**: データベースが空になった場合に`Hello World`を自動復旧
- **データ永続化**: SQLiteによるローカルデータベース管理
- **入力検証**: 空白のみのメッセージは登録・更新不可

## 特徴

- **実用的なアプリ構成**: データベース連携を含む実際のアプリケーション構造
- **拡張性**: 新しい機能を追加しやすい設計（DAO パターン使用）
- **詳細な日本語コメント**: 初学者向けのJavadocコメント
- **Maven対応**: 依存関係の管理とビルドが簡単

## 必要な環境

- **Java**: Java 24 以上
- **Maven**: 3.6.0 以上（Maven Wrapperを使用する場合は不要）
- **OS**: Windows 10/11（他のOSでも動作可能）

## セットアップ手順

### 1. リポジトリのクローン

```bash
git clone https://github.com/TakuyaFukumura/basic-windows-app.git
```

```bash
cd basic-windows-app
```

### GitHub Copilot用スキル

`.github/skills/` に、リポジトリの改修、PRマージ・リリース、スキル改善を支援する手順を用意しています。Java/Maven/JavaFXの構成と、このリポジトリのブランチ・検証・安全ルールに合わせて記載しています。

### 2. 依存関係のインストール

#### Maven Wrapperを使用する場合（推奨）

```bash
./mvnw clean install
```

#### 通常のMavenを使用する場合

```bash
mvn clean install
```

## 実行方法

### 方法1: Maven JavaFXプラグインを使用（推奨）

#### Maven Wrapperを使用する場合

```bash
./mvnw javafx:run
```

#### 通常のMavenを使用する場合

```bash
mvn javafx:run
```

### 方法2: Javaコマンドを直接使用

まずプロジェクトをコンパイルします：

#### Maven Wrapperを使用する場合

```bash
./mvnw clean compile
```

#### 通常のMavenを使用する場合

```bash
mvn clean compile
```

次に、JavaFXモジュールを指定してアプリケーションを実行します：

```bash
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp "target/classes:path/to/sqlite-jdbc.jar" com.example.basicwindowsapp.BasicWindowsApp
```

**注意**: `path/to/javafx/lib`はJavaFX SDKのライブラリパス、
`path/to/sqlite-jdbc.jar`はMavenから取得したSQLite JDBC JARのパスに置き換えてください。
Windowsではクラスパスの区切り文字に`;`を使用し、macOS/Linuxでは`:`を使用します。
例えばWindowsでは次のように実行します：

```cmd
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp "target/classes;path/to/sqlite-jdbc.jar" com.example.basicwindowsapp.BasicWindowsApp
```

### 方法3: JARファイルの作成

#### Maven Wrapperを使用する場合

```bash
./mvnw clean package
```

#### 通常のMavenを使用する場合

```bash
mvn clean package
```

`target`ディレクトリにJARファイルが作成されます。
標準のJARにはアプリケーションのメインマニフェスト属性がないため、
アプリケーションの起動には方法1の`javafx:run`を使用してください。

## プロジェクト構造

```
basic-windows-app/
├── pom.xml                                    # Mavenビルド設定
├── README.md                                  # このファイル
├── .gitignore                                 # Git無視ファイル設定
├── mvnw                                       # Maven Wrapper実行スクリプト（Unix/Linux/Mac用）
├── mvnw.cmd                                   # Maven Wrapper実行スクリプト（Windows用）
├── .mvn/                                      # Maven Wrapper設定
│   └── wrapper/
│       └── maven-wrapper.properties          # Maven Wrapperプロパティ
└── src/
    └── main/
        ├── java/
        │   └── com/example/basicwindowsapp/
        │       ├── BasicWindowsApp.java        # JavaFX UIとイベント処理
        │       ├── model/
        │       │   └── Message.java            # メッセージエンティティ
        │       └── dao/
        │           ├── DatabaseManager.java    # SQLite接続・初期化
        │           └── MessageDao.java         # メッセージCRUD
        └── resources/                          # リソースファイル用ディレクトリ
```

アプリケーションの起動時にプロジェクトの実行ディレクトリへ`basicwindowsapp.db`が作成されます。
このファイルは`.gitignore`で除外され、メッセージはアプリケーションの再起動後も保持されます。

## Maven Wrapperについて

このプロジェクトはMaven Wrapperを使用しており、Maven本体をインストールしなくてもプロジェクトをビルド・実行できます。

### Maven Wrapperの利点

- **環境依存なし**: 特定のMavenバージョンに依存せず、プロジェクト固有のMavenバージョンを使用
- **簡単セットアップ**: Javaさえインストールされていれば、追加のセットアップなしでビルド可能
- **一貫した環境**: チーム開発において全員が同じMavenバージョンを使用可能

### Maven Wrapperの使用方法

#### Windows環境の場合

```cmd
mvnw.cmd clean install
mvnw.cmd javafx:run
```

#### Unix/Linux/Mac環境の場合

```bash
./mvnw clean install
./mvnw javafx:run
```

**注意**: 初回実行時には、指定されたMavenバージョンが自動的にダウンロードされるため、インターネット接続が必要です。

## 主要なファイルの説明

### BasicWindowsApp.java

メインのアプリケーションクラスです。JavaFXの`Application`クラスを継承し、以下の機能を提供します：

- **ウィンドウの作成**: 初期サイズ800x600、最小サイズ600x400のリサイズ可能なウィンドウ
- **現在のメッセージ表示**: 最新メッセージを上部に大きく表示
- **メッセージ一覧**: ID、本文、作成日時をTableViewに表示
- **操作UI**: 新規作成、編集、削除、更新の各操作に対応

### DatabaseManager.java

SQLiteへの接続を管理し、`messages`テーブルを初回起動時に作成します。
テーブルが空の場合は、初期データとして`Hello World`を登録します。

### MessageDao.java

DAOパターンでデータアクセスを分離し、以下の操作を提供します：

- メッセージの登録、取得、更新、削除
- 最新メッセージの取得
- メッセージ件数の取得
- 全メッセージ削除後のデフォルトメッセージ復旧

### データベース設計

```sql
CREATE TABLE messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    text TEXT NOT NULL,
    created_at INTEGER NOT NULL
);
```

### pom.xml

Maven設定ファイルです。以下の設定が含まれています：

- **Java 24対応**: JavaFX 26.0.2を使用可能
- **JavaFX依存関係**: JavaFX ControlsとFXMLライブラリ
- **SQLite JDBC**: SQLite 3.42.0.0によるローカルデータ永続化
- **プラグイン設定**: コンパイルと実行用の設定

## 開発ガイド

### 新機能の追加

1. **新しいクラスの追加**: `src/main/java/com/example/basicwindowsapp/`ディレクトリに新しいJavaファイルを作成
2. **リソースファイルの追加**: `src/main/resources/`ディレクトリにFXMLファイルや画像などを配置
3. **依存関係の追加**: 必要に応じて`pom.xml`に新しい依存関係を追加

## トラブルシューティング

### JavaFXランタイムが見つからない場合

Java 11以降では、JavaFXはJDKから分離されています。以下の対処法があります：

1. **JavaFX SDKのダウンロード**: [OpenJFX公式サイト](https://openjfx.io/)からダウンロード
2. **環境変数の設定**: `PATH_TO_FX`環境変数にJavaFXライブラリパスを設定
3. **IDEの設定**: IntelliJ IDEAやEclipseでJavaFXライブラリを設定

### ビルドエラーが発生する場合

#### Maven Wrapperを使用する場合

```bash
./mvnw clean
```

```bash
./mvnw compile
```

#### 通常のMavenを使用する場合

```bash
mvn clean
```

```bash
mvn compile
```

で依存関係をクリアしてから再ビルドしてください。

## CI/CD設定

このプロジェクトはGitHub Actionsを使用してCI/CDを自動化しています。

### 自動化された処理

- **ビルドテスト**: プッシュやプルリクエスト時に自動でコンパイルテストを実行
- **クロスプラットフォームテスト**: Ubuntu、Windows、macOSでのビルド確認
- **依存関係のキャッシュ**: Mavenの依存関係をキャッシュして高速化

### ワークフロー詳細

詳細な設定は `.github/workflows/ci.yml` ファイルをご確認ください。
