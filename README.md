# AWS SDK for Java 2 を使用して Amazon S3 を操作するサンプル

This project contains a maven application with AWS Java SDK 2.x dependencies.

[AWS Java SDK 2.x API Doc](https://sdk.amazonaws.com/java/api/latest/index.html) 

## サンプルを作成するうえで参考にしたドキュメント

https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/java_s3_code_examples.html

https://github.com/aws/aws-sdk-java-v2/issues/3554

https://repost.aws/ja/questions/QUkm9_BilaRiWi9xtCgACKrQ/checking-for-existence-of-s3-bucket-java-aws-sdk-2


## 要件
- Java 1.8+
- Apache Maven

## プロジェクトの構造

```
s3basic
├── src
│   ├── main
│   │   ├── java
│   │   │   └── example.s3
│   │   │       ├── CreateBucketExample.java
│   │   │       ├── DeleteBucketExample.java
│   │   │       ├── DeleteObjectExample.java
│   │   │       ├── GetObjectExample.java
│   │   │       ├── HeadBucketExample.java
│   │   │       ├── ListBucketsExample.java
│   │   │       └── PutObjectExample.java
│   │   └── resources
│   │       ├── config.properties
│   │       └── simplelogger.properties
```

- `CreateBucketExample.java`: バケットの作成
- `ListBucketsExample.java`: バケット一覧の表示
- `HeadBucketExample.java`: バケットの存在チェック
- `PutObjectExample.java`: オブジェクトの格納
- `GetObjectExample.java`: オブジェクトの取得
- `DeleteObjectExample.java`: オブジェクトの削除
- `GeneratePresignedURLExample.java`: 署名付き URL の作成
- `DeleteBucketExample.java`: バケットの削除


#### Compile and Execution

- config.properties の aws_region に使用する AWS リージョンを指定して下さい。
- config.properties の bucket_name にユニークなバケット名を指定して下さい。

- pom.xml が存在するフォルダに移動してから下記でコンパイルします

```
mvn compile
```

- コンパイルが成功したら下記で実行します。

```
mvn exec:java -Dexec.mainClass="example.s3.(クラス名)"
```

- 例
```
mvn exec:java -Dexec.mainClass="example.s3.CreateBucketExample"
```


#### Building the project
```
mvn clean package
```
