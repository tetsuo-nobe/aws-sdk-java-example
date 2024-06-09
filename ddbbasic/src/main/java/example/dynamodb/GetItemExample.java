package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;



public class GetItemExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Geting your item from Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
            
            // Get する Item のキーの準備
            String userId = "1";
            String gameId = "G001";
            
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("userId", AttributeValue.builder().n(userId).build());
            itemValues.put("gameId", AttributeValue.builder().s(gameId).build());
            
            // GetItemRequest の作成 （デフォルト 結果整合性）
            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(itemValues)
                    .build();
            
            // getItem の実行(該当する Item が無い場合は何も返さない)
            Map<String, AttributeValue> returnedItem = client.getItem(request).item();  
            if (returnedItem.isEmpty()) {
                System.out.format("No item found with the key %s , %s \n", userId,gameId);
            }
            else {
                // 属性名の Set を取得してから値を取得
                Set<String> keys = returnedItem.keySet();
                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
                // 属性名を指定して値を取得
                String pkey = "userId";
                String skey = "gameId";
                String score = "score";
                String life = "life";
                System.out.format("%s: %s \n",pkey,returnedItem.get(pkey).n());
                System.out.format("%s: %s \n",skey,returnedItem.get(skey).s());
                System.out.format("%s: %s \n",score,returnedItem.get(score).n());
                System.out.format("%s: %s \n",life,returnedItem.get(life).n());
                
            }    
            
            // 強力な整合性のある読み込み + 属性の指定
            System.out.println("--- Use consistentRead and projectionExpression ---");
            // GetItemRequest の作成
            GetItemRequest request2 = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(itemValues)
                    .consistentRead(true)
                    .projectionExpression("gameId,score")
                    .build();
            
            // getItem の実行(該当する Item が無い場合は何も返さない)
            Map<String, AttributeValue> returnedItem2 = client.getItem(request2).item();  
            if (returnedItem2.isEmpty()) {
                System.out.format("No item found with the key %s , %s \n", userId,gameId);
            }
            else {
                // 属性名の Set を取得してから値を取得
                Set<String> keys = returnedItem.keySet();
                for (String key1 : keys) {
                    if (key1.equals("gameId")) {
                        System.out.format("%s: %s\n", key1, returnedItem.get(key1).s());
                    }
                    else {
                        System.out.format("%s: %s\n", key1, returnedItem.get(key1).n());
                    }
                }
                
                
            }    
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

