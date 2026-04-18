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
# イベント情報を取得して保存
./gradlew runEvent

# イベント結果を取得して保存（日付未指定は task.conf の設定を使用） (Depend on runEvent)
./gradlew runEventResult

# イベント結果を取得して保存（日付指定）(Depend on runEvent)
./gradlew runEventResult -Dfrom=2026-04-01 -Dto=2026-04-18

# デッキ内カード情報を取得して保存（日付未指定は当日1日分）
./gradlew runCardInDeck

# デッキ内カード情報を取得して保存（日付指定）
./gradlew runCardInDeck -Dfrom=2026-02-16 -Dto=2026-02-19

# デッキ画像を Eagle にインポート（既存の画像はスキップ）
./gradlew fetchDeckImages

# DBマイグレーション
./gradlew migrateDatabase
```

## 設定

`src/main/resources/application.conf` にデータベース接続情報を記載する。

`src/main/resources/task.conf` にタスクの実行パラメータ（対象期間・イベントIDなど）を記載する。

`image-fetcher.eagle-folder-id` に Eagle のインポート先フォルダIDを指定する。フォルダIDは Eagle でフォルダを右クリック → 「フォルダリンクをコピー」で取得できる（`eagle://folder/XXXXXXXX` の末尾部分）。
