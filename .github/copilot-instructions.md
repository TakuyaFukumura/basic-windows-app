# Basic Windows App - GitHub Copilot 指示書

Basic Windows App は、GUIウィンドウに「Hello World」を表示するシンプルなJavaFXデスクトップアプリケーションです。JavaベースのWindowsアプリケーション開発のテンプレートとして機能します。

**常にこの指示書を最初に参照し、ここに記載された情報が不完全または誤りであることが判明した場合のみ、検索やbashコマンドにフォールバックしてください。**

## 効果的な作業方法

### 環境要件
- **Java**: Java 17以上（OpenJDK 17.0.16+で動作確認済み）
- **Maven**: 3.6.0以上（Maven 3.9.11+で動作確認済み）
- **OS**: Windows、Linux、macOSで動作
- **ディスプレイ**: JavaFXアプリケーションはGUI実行のためディスプレイ環境が必要

### 必須ビルドコマンド
新しいリポジトリセットアップ時は、必ずこれらのコマンドを順番に実行してください：

```bash
# プロジェクトルートに移動
cd /path/to/basic-windows-app

# 依存関係を含むクリーンインストール - 絶対にキャンセルしないこと：約20秒かかります
mvn clean install
```

**重要なタイミング**: `mvn clean install`は通常20秒で完了しますが、タイムアウトを60分以上に設定してください。Mavenビルドは絶対にキャンセルしないでください。

### 主要ビルドコマンド
- `mvn clean compile` - ソースコードをコンパイル（約2秒）
- `mvn clean package` - JARファイルを作成（約3秒）
- `mvn clean install` - 依存関係を含む完全ビルド（約20秒、絶対にキャンセルしないこと：60分以上のタイムアウトを設定）

### アプリケーションの実行

**重要**: JavaFXアプリケーションはグラフィカルディスプレイ環境が必要で、ヘッドレス環境（CI、X11なしのDocker、ディスプレイ転送なしのリモートターミナル）では失敗します。

#### 方法1: Maven JavaFXプラグイン（推奨）
```bash
mvn javafx:run
```
**注意**: ヘッドレス環境では「UnsupportedOperationException: Unable to open DISPLAY」で失敗します。これは期待される動作であり、ビルド失敗ではありません。

#### 方法2: Javaコマンド直接実行
```bash
# まずコンパイル
mvn clean compile

# JavaFXモジュールを指定して実行（別途JavaFX SDKのインストールが必要）
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.example.basicwindowsapp.BasicWindowsApp
```

#### 方法3: JAR実行
```bash
# JARを作成
mvn clean package

# JAR場所: target/basic-windows-app-0.1.0.jar
# 注意: 標準JARにはメインマニフェスト属性がありません - mvn javafx:runを使用してください
```

## 検証とテスト

### ビルド検証
変更後は必ずこれらの検証手順を実行してください：
1. `mvn clean compile` - コードがコンパイルされることを確認
2. `mvn clean package` - JARファイルが作成されることを確認
3. `mvn clean install` - 完全ビルドの検証

### アプリケーション検証
**手動検証要件**: UI変更を行う際は、実際のアプリケーション機能をテストする必要があります：

1. **アプリケーション実行**: `mvn javafx:run`（ディスプレイ環境が必要）
2. **期待される動作**:
   - タイトル「Basic Windows App - Hello World」でウィンドウが開く
   - ウィンドウサイズ: 初期値400x300ピクセル
   - 「Hello World」テキストが中央に表示、大きな太字フォント（24px）
   - ウィンドウはリサイズ可能、最小サイズ300x200ピクセル
3. **テストシナリオ**:
   - ウィンドウが正しく開いて表示されることを確認
   - ウィンドウリサイズ機能をテスト
   - テキストが適切に中央配置され、スタイルが適用されていることを確認
   - ウィンドウクローズ機能をテスト

### テストは利用不可
このプロジェクトには現在ユニットテストがありません。`mvn test`フェーズは実行されますが「No tests to run.」と報告されます。

### リンター/フォーマットツールなし
Mavenフォーマットやリンタープラグインは現在設定されていません。標準的なMavenコンパイル警告のみがコード品質チェックとして利用可能です。

## プロジェクト構造と主要ファイル

### リポジトリルート構造
```
basic-windows-app/
├── .github/                           # GitHub設定
├── docs/                              # ドキュメント
│   └── DESIGN.md                      # UI設計仕様
├── src/
│   └── main/
│       └── java/
│           └── com/example/basicwindowsapp/
│               └── BasicWindowsApp.java    # メインアプリケーションクラス
├── pom.xml                            # Maven設定
├── README.md                          # 日本語ドキュメント
├── test-app.sh                        # テストスクリプト（参考用のみ）
└── .gitignore                         # Git除外ルール
```

### 主要ファイル
- **BasicWindowsApp.java**: Applicationを継承するメインJavaFXアプリケーションクラス
- **pom.xml**: Java 17、JavaFX 21依存関係を含むMavenビルド設定
- **README.md**: 包括的な日本語ドキュメント
- **docs/DESIGN.md**: UI仕様と技術要件

### 依存関係
- **JavaFX Controls**（バージョン21）: コアJavaFX UIコンポーネント
- **JavaFX FXML**（バージョン21）: FXMLマークアップサポート（現在未使用）

## 一般的な開発タスク

### 新機能の追加
1. **新しいJavaクラス**: `src/main/java/com/example/basicwindowsapp/`に追加
2. **リソース**: `src/main/resources/`に追加（必要に応じてディレクトリを作成）
3. **依存関係**: `pom.xml`の依存関係セクションに追加

### ビルドトラブルシューティング
ビルドが失敗した場合：
```bash
mvn clean
mvn compile
```

### JavaFXランタイムの問題
JavaFXランタイムエラーが発生した場合：
- Java 17+がインストールされていることを確認
- JavaFXはMaven依存関係に含まれている（別途SDKは不要）
- 手動Java実行の場合は、https://openjfx.io/ からJavaFX SDKをダウンロード

## タイミング期待値とタイムアウト設定

**重要**: Mavenコマンドの早期キャンセルを防ぐため、適切なタイムアウトを常に設定してください：

- `mvn clean compile`: 約2秒（5分以上のタイムアウトを設定）
- `mvn clean package`: 約3秒（5分以上のタイムアウトを設定）
- `mvn clean install`: 約20秒（60分以上のタイムアウトを設定）
- `mvn javafx:run`: ヘッドレス環境では即座に失敗

**Mavenビルドを絶対にキャンセルしないこと**: 高速ビルドでも、ネットワークの問題やシステム負荷により時間がかかることがあります。

## CI/ビルドパイプラインの注意事項
- 現在GitHub Actionsワークフローは存在しません
- JavaFXアプリケーションはディスプレイ設定なしの標準CI環境では実行できません
- ビルド検証には`mvn clean install`のみを使用
- CIで`mvn javafx:run`を試行しないでください - 常に失敗します

## 開発環境セットアップ
新しい開発者向け：
1. Java 17+ JDKをインストール
2. Maven 3.6.0+をインストール
3. リポジトリをクローン
4. `mvn clean install`を実行してセットアップを確認
5. JavaFXサポートのあるIDEを使用（IntelliJ IDEA、e(fx)clipseを使用したEclipse）

## 予想されるエラーメッセージ
- **「UnsupportedOperationException: Unable to open DISPLAY」**: ヘッドレス環境では正常
- **「no main manifest attribute」**: 標準JARを実行しようとした場合 - `mvn javafx:run`を使用してください
- **JavaFX警告**: ビルド中のJavaFX依存関係警告は正常です
