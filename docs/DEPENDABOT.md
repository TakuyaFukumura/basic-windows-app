# Dependabot設定について

## 概要

このプロジェクトでは、Dependabotを使用してMaven依存関係の自動更新を行っています。

## Dependabotとは

Dependabotは、GitHubが提供する依存関係管理ツールで、以下の機能を提供します：

- 依存関係の自動チェック
- セキュリティアップデートの自動検出
- プルリクエストの自動作成
- 脆弱性アラートの通知

## 設定内容

### 更新スケジュール
- **頻度**: 毎週月曜日
- **時刻**: 09:00 (Asia/Tokyo)
- **対象**: Maven依存関係

### プルリクエスト設定
- **最大同時プルリクエスト数**: 5個
- **ラベル**: `dependencies`, `java`
- **コミットメッセージプレフィックス**: `deps`

## 運用方法

### プルリクエストの確認
1. Dependabotが作成したプルリクエストを確認
2. 変更内容とChangelog/リリースノートを確認
3. ローカルでビルドとテストを実行
4. 問題がなければマージ

### 除外設定
特定の依存関係を更新対象から除外したい場合は、`.github/dependabot.yml`の`ignore`セクションに追加してください。

### レビュアー設定
チームでの運用を行う場合は、`.github/dependabot.yml`の`reviewers`セクションにレビュアーを設定できます。

## トラブルシューティング

### よくある問題

1. **プルリクエストが作成されない**
   - 依存関係が最新の場合は作成されません
   - 設定ファイルの構文エラーがないか確認してください

2. **ビルドエラーが発生する**
   - 依存関係間の互換性問題の可能性があります
   - 段階的にアップデートを行うことを検討してください

3. **セキュリティアラートが表示される**
   - 速やかに依存関係を更新してください
   - 必要に応じて緊急パッチを適用してください

## 関連リンク

- [GitHub Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Maven Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
