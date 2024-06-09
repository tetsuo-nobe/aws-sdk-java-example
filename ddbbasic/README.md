# AWS SDK for Java 2 を使用して Amazon DynamoDB を操作するサンプル

This project contains a maven application with AWS Java SDK 2.x dependencies.

[AWS Java SDK 2.x API Doc](https://sdk.amazonaws.com/java/api/latest/index.html) 

## サンプルを作成するうえで参考にしたドキュメント

https://docs.aws.amazon.com/ja_jp/sdk-for-java/latest/developer-guide/java_dynamodb_code_examples.html

https://dynobase.dev/code-examples/dynamodb-conditional-update-java/

https://stackoverflow.com/questions/59852120/how-do-i-scan-dynamodb-using-aws-sdk-v2-for-java


## 要件
- Java 1.8+
- Apache Maven

## プロジェクトの構造

```
ddbbasic
├── src
│   ├── main
│   │   ├── java
│   │   │   └── example.dynamodb
│   │   │       ├── CreateTableExample.java
│   │   │       ├── DeleteItemExample.java
│   │   │       ├── DeleteTableExample.java
│   │   │       ├── GetItemExample.java
│   │   │       ├── ListTablesExample.java
│   │   │       ├── PutItemExample.java
│   │   │       ├── QueryExample.java
│   │   │       ├── ScanExample.java
│   │   │       ├── ScanPaginatorExample.java
│   │   │       └── UpdateItemExample.java
│   │   └── resources
│   │       ├── config.properties
│   │       └── simplelogger.properties
```

- `CreateTableExample`: テーブルの作成
- `DeleteItemExample.java`: 項目の削除
- `DeleteTableExample.java`: テーブルの削除
- `GetItemExample.java`: 項目の取得
- `ListTablesExample.java`: テーブルの一覧取得
- `PutItemExample.java`: 項目の作成
- `QueryExample.java`: 項目の作成
- `ScanExample.java`: 項目の作成
- `ScanPaginatorExample.java`: 項目の作成
- `UpdateItemExample.java`: 項目の更新


#### Compile and Execution

- config.properties の aws_region に使用する AWS リージョンを指定して下さい。
- config.properties の table_name が使用するリージョンでユニークか確認してください。

- pom.xml が存在するフォルダに移動してから下記でコンパイルします

```
mvn compile
```

- コンパイルが成功したら下記で実行します。

```
mvn exec:java -Dexec.mainClass="example.dynamodb.(クラス名)"
```

- 例
```
mvn exec:java -Dexec.mainClass="example.dynamodb.CreateTableExample"
```


#### Building the project
```
mvn clean package
```