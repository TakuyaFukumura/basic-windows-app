# Basic Windows App

JavaFXとSQLiteを使用したメッセージ管理アプリケーションのテンプレートリポジトリです。

## 概要

このプロジェクトは、JavaFXとSQLiteデータベースを使用してメッセージの表示・編集・削除・登録を行うGUIアプリケーションです。
Windowsアプリ開発における基本的なCRUD操作のベースとして使用することができます。

## 機能

- **メッセージ表示**: SQLiteデータベースから取得したメッセージを画面に表示
- **CRUD操作**: メッセージの新規作成・編集・削除・一覧表示
- **メッセージ管理**: TableViewを使用したメッセージ一覧と操作UI
- **デフォルト復旧**: 全メッセージ削除時のデフォルトメッセージ自動復旧
- **データ永続化**: SQLiteによるローカルデータベース管理

## 特徴

- **実用的なアプリ構成**: データベース連携を含む実際のアプリケーション構造
- **拡張性**: 新しい機能を追加しやすい設計（DAO パターン使用）
- **詳細な日本語コメント**: 初学者向けのJavadocコメント
- **Maven対応**: 依存関係の管理とビルドが簡単

## 必要な環境

- **Java**: Java 17 以上
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
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.example.basicwindowsapp.BasicWindowsApp
```

**注意**: `path/to/javafx/lib`は、お使いの環境のJavaFXライブラリパスに置き換えてください。

### 方法3: 実行可能JARファイルの作成

#### Maven Wrapperを使用する場合

```bash
./mvnw clean package
```

#### 通常のMavenを使用する場合

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
├── mvnw                                       # Maven Wrapper実行スクリプト（Unix/Linux/Mac用）
├── mvnw.cmd                                   # Maven Wrapper実行スクリプト（Windows用）
├── .mvn/                                      # Maven Wrapper設定
│   └── wrapper/
│       └── maven-wrapper.properties          # Maven Wrapperプロパティ
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── basicwindowsapp/
        │               └── BasicWindowsApp.java   # メインアプリケーションクラス
        └── resources/                         # リソースファイル用ディレクトリ
```

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


