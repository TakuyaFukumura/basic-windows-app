# 機能拡張ロードマップ

## 目的

`basic-windows-app` は、JavaFX と SQLite を使った Windows デスクトップアプリケーションのテンプレートです。
本書では、現在の実装を基準に、テンプレートとしての再利用性を高めるための次の拡張を整理します。

ここに記載する内容は確定した実装計画ではなく、優先順位を付けるための候補です。実装時は、既存の
シンプルさ、Windows 以外でもビルドできること、依存関係を増やしすぎないことを優先します。

## 現在の実装

### 提供している機能

- Java 24、Maven、JavaFX 26.0.2 によるデスクトップアプリケーション
- SQLite によるローカルデータ永続化
- `messages` テーブルを対象としたメッセージの登録・取得・更新・削除
- 最新メッセージの表示と、ID・本文・作成日時を表示する `TableView`
- メッセージ本文の大文字・小文字を区別しない一覧検索
- 空白だけのメッセージを登録・更新しない入力検証
- CSV／UTF-8テキストのインポート・エクスポート
- ファイル選択またはドラッグ＆ドロップによるインポート
- エクスポート時の上書き確認
- ファイル入出力とインポート登録のバックグラウンド実行
- ファイル入出力中のステータス表示と進捗インジケーター
- 起動時のデータベース・テーブル初期化
- データが空になった場合の `Hello World` メッセージ自動復旧
- ライトモード／ダークモードの切替と、情報・警告・確認・入力ダイアログ
- テーマ、ウィンドウサイズ、ウィンドウ位置の保存と復元
- `TabPane` によるメッセージ管理画面とアプリ情報画面の切替
- メニューバー、操作ボタン、キーボードショートカットによる操作
- Maven Wrapper、JUnit 5、GitHub Actions によるビルド・テスト
- `jpackage` プロファイルによるアプリケーションイメージ作成

### 現在の構成

```text
src/
├── main/
│   ├── java/com/example/basicwindowsapp/
│   │   ├── BasicWindowsApp.java          # JavaFX UI とイベント処理
│   │   ├── config/ApplicationSettings.java # 設定の保存と復元
│   │   ├── dao/
│   │   │   ├── DatabaseManager.java      # SQLite 接続と初期化
│   │   │   └── MessageDao.java           # メッセージ CRUD
│   │   ├── io/MessageFileService.java   # テキスト／CSV 入出力
│   │   ├── model/Message.java            # メッセージモデル
│   │   └── validation/MessageValidator.java # 入力検証と正規化
│   └── resources/styles.css              # ライト／ダークテーマ
└── test/java/com/example/basicwindowsapp/
    ├── config/ApplicationSettingsTest.java
    ├── io/MessageFileServiceTest.java
    └── validation/MessageValidatorTest.java
```

アプリケーションデータは、実行ディレクトリではなくユーザーのホームディレクトリ配下の
`.basic-windows-app\basicwindowsapp.db` に保存されます。テーマとウィンドウ設定は同じディレクトリの
`.basic-windows-app\settings.properties` に Java Properties 形式で保存されます。

### ビルドと配布

```text
mvnw.cmd clean install              # Windows の完全ビルド
mvnw.cmd javafx:run                 # GUI を起動
mvnw.cmd clean package -Pjpackage   # Windows アプリケーションイメージ
```

CI では Ubuntu で `clean compile`、`test`、`package` を実行し、Windows と macOS では
`clean compile` を実行します。GUI 起動はディスプレイが必要なため CI の対象外です。

## 拡張方針

### 優先度 1: UI コンポーネントのサンプル

テンプレートとしての学習価値を高めるため、既存のメッセージ管理を壊さず、実際のユースケースに必要なものから追加します。

- `TableView` の編集、複数選択、フィルター、CSV 出力の強化
- `Chart` による基本的なデータ可視化
- ステータス表示と進捗表示

FXML を導入する場合は、画面・コントローラー・モデルの責務と、プログラムによる UI 構築との使い分けを
先に決めます。小さな画面まで一律に FXML 化することは避けます。

### 優先度 2: 国際化と運用

基本機能を維持しながら、配布・運用を見据えた機能を追加します。

- `ResourceBundle` による日本語・英語のメッセージ管理
- バージョン情報、診断情報、ログ保存場所の表示
- アプリケーション設定とユーザーデータのバックアップ／復元
- Windows 向けインストーラー（MSI）とショートカット設定
- 更新通知と更新手順の提供

外部ライブラリを追加する場合は、用途、ライセンス、保守状況、Windows 配布時の影響を確認してから採用します。
自動更新は、署名、配布元の信頼性、失敗時の復旧方法を含めて設計できる段階になってから検討します。

## 実装時の受け入れ基準

新しい機能を追加する際は、少なくとも次を満たします。

1. 既存の `mvnw.cmd clean install` が成功すること。
2. UI に依存しないロジックには自動テストを追加すること。
3. Windows で `mvnw.cmd javafx:run` を実行したとき、既存のメッセージ管理が使えること。
4. 既存データを破壊せず、データ形式を変更する場合は移行手順を用意すること。
5. 追加した依存関係、保存先、権限、ライセンスを README または関連ドキュメントに記載すること。
6. CI で確認できない GUI 操作は、手動確認手順をドキュメント化すること。

## 対象外とすること

テンプレートの目的から、次の機能は当面の標準機能には含めません。

- サーバー、クラウドデータベース、ユーザー認証
- 特定の業務ドメインに依存する画面やデータモデル
- 複数の UI フレームワークの同時採用
- 要件がない状態での大規模な依存関係追加
- CI 上での GUI 起動テスト

これらが必要なアプリケーションでは、本テンプレートを基に要件に合わせて構成を分岐させます。

## 変更時に確認するファイル

機能拡張の内容に応じて、次のファイルを同時に確認・更新します。

- `src/main/java/com/example/basicwindowsapp/BasicWindowsApp.java`
- `src/main/java/com/example/basicwindowsapp/model/`
- `src/main/java/com/example/basicwindowsapp/dao/`
- `src/main/resources/`
- `src/test/java/com/example/basicwindowsapp/`
- `pom.xml`
- `README.md`
- `.github/workflows/ci.yml`

実装されていないクラス名やリソースパスを、あらかじめ「実装ファイル」として記載することは避けます。
実際の変更が発生した時点で構成図とドキュメントを更新し、常にリポジトリの状態と一致させます。
