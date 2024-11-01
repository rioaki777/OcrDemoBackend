[Qiita の記事](https://qiita.com/RioAki/items/639d7ab5f6afbc59e7d8) にて紹介しているプロジェクトのバックエンドです。

## Config

/src/main/resources/application.conf にて下記を適宜設定してください。

```
# クライアントからアップされたファイルを配置するフォルダ
uploadDir = "C:/TestFolder"

# python.exeのパス
pythonDir = "C:/.../python.exe"
```


## Build
```
./gradlew build
```

## Running
```
./gradlew run
```
