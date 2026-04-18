# pcea

ポケモンカード公式サイトのイベント結果を収集・管理するサーバーアプリケーション。

## 起動方法

### サーバー

```bash
./gradlew run
```

### タスク（任意のタイミングで実行）

サーバーとは別のターミナルで実行する。

```bash
# イベント結果を取得して保存
./gradlew runEventResult

# デッキ内カード情報を取得して保存
./gradlew runCardInDeck

# DBマイグレーション
./gradlew migrateDatabase
```

## 設定

`src/main/resources/application.conf` にデータベース接続情報を記載する。

`src/main/resources/task.conf` にタスクの実行パラメータ（対象期間・イベントIDなど）を記載する。
