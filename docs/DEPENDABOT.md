# Dependabot設定について

## 概要

このプロジェクトでは、Dependabotを使用してMaven依存関係とGitHub Actionsの自動更新を行っています。

## Dependabotとは

Dependabotは、GitHubが提供する依存関係管理ツールで、以下の機能を提供します：

- 依存関係の自動チェック
- セキュリティアップデートの自動検出
- プルリクエストの自動作成
- 脆弱性アラートの通知

## 設定内容

### Maven依存関係の更新スケジュール
- **頻度**: 毎週月曜日
- **時刻**: 09:00 (Asia/Tokyo)
- **対象**: Maven依存関係（pom.xml）
- **最大同時プルリクエスト数**: 5個
- **ラベル**: `dependencies`, `java`

### GitHub Actionsの更新スケジュール
- **頻度**: 毎週月曜日
- **時刻**: 09:30 (Asia/Tokyo)
- **対象**: GitHub Actionsワークフロー（.github/workflows/）
- **最大同時プルリクエスト数**: 3個
- **ラベル**: `dependencies`, `github-actions`

### 共通設定
- **コミットメッセージプレフィックス**: `deps`
- **タイムゾーン**: Asia/Tokyo
- **スコープ情報**: 含む

## 運用方法

### プルリクエストの確認手順
1. Dependabotが作成したプルリクエストを確認
2. 変更内容とChangelog/リリースノートを確認
3. ローカルでビルドとテストを実行
4. 問題がなければマージ

### Maven依存関係の更新
- JavaFX関連ライブラリの更新
- Mavenプラグインの更新
- セキュリティ脆弱性のある依存関係の緊急更新

### GitHub Actionsの更新
- actions/checkout の新しいバージョン
- actions/setup-java の新しいバージョン
- actions/cache の新しいバージョン
- その他使用中のアクションの更新

## 除外設定

特定の依存関係やアクションを更新対象から除外したい場合は、`.github/dependabot.yml`の`ignore`セクションに追加してください。

例：
```yaml
ignore:
  - dependency-name: "org.example:library"
    versions: ["1.x"]
  - dependency-name: "actions/checkout"
    versions: ["v3"]
```

## レビュアー設定

チームでの運用を行う場合は、`.github/dependabot.yml`の`reviewers`セクションにレビュアーを設定できます。

## トラブルシューティング

### よくある問題

1. **プルリクエストが作成されない**
   - 依存関係やアクションが最新の場合は作成されません
   - 設定ファイルの構文エラーがないか確認してください

2. **ビルドエラーが発生する**
   - Maven依存関係間の互換性問題の可能性があります
   - 段階的にアップデートを行うことを検討してください

3. **GitHub Actionsワークフローエラー**
   - アクションのAPIの変更により互換性問題が発生する可能性があります
   - ワークフローファイルの修正が必要な場合があります

4. **セキュリティアラートが表示される**
   - 速やかに依存関係を更新してください
   - 必要に応じて緊急パッチを適用してください

## 監視とメンテナンス

### 定期的な確認事項
- プルリクエストの確認と適切なマージ
- 失敗したビルドの調査と修正
- セキュリティアラートへの迅速な対応
- 除外設定の定期的な見直し

## 関連リンク

- [GitHub Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Maven Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
