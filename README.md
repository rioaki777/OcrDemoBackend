[Qiita の記事](To be updated) にて紹介しているプロジェクトのバックエンドです。

## Config

/src/main/resources/application.conf にて下記を適宜設定してください。

```
# クライアントからアップされたファイルを配置するフォルダ
uploadDir = "C:/TestFolder"

# python.exeのパス
pythonDir = "C:/.../python.exe"
```


## Build
```bash
./gradlew build
```

## Running
```bash
./gradlew run
```
