package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.util.HashMap;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;


public class QueryExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Query your item from Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
            
            // Query の条件となるキーの準備
            String userId = "1";
            
            // キーのプレースホルダーとその値を設定
            HashMap<String, AttributeValue> attrValues = new HashMap<>();
            attrValues.put(":uId", AttributeValue.builder()
                    .n(userId)
                    .build());

            // QueryRequest の作成
            QueryRequest queryReq = QueryRequest.builder()
                    .tableName(tableName)
                    .keyConditionExpression("userId = :uId")
                    .expressionAttributeValues(attrValues)
                    .projectionExpression("gameId,score")
                    .build();
            // query の実行        
            QueryResponse response = client.query(queryReq);
            
            // 件数の取得
            int count = response.count();
            System.out.println("There were " + count + "  record(s) returned");
            
            // 結果を表示
            response.items().forEach(item -> System.out.format("gameId = %s, score=%s\n", item.get("gameId").s(),item.get("score").n()));


        } 
        catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        finally {
            client.close();
        }
        
    }

    // config.properties からプロパティを取得するユーティリティメソッド
    public static Properties readProperties() throws Exception {
        InputStream configFile = ListTablesExample.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try { properties.load(configFile); }
        catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
        catch (IOException ioe) { ioe.printStackTrace(); }
        return properties;
    }
 
}

