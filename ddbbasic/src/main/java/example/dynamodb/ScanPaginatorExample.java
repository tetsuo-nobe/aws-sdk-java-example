package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;

public class ScanPaginatorExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Scan your item from Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
            
            // ScanRequest の作成
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .limit(3)
                    .build();
                    
            // scan の実行
            //ScanResponse response = client.scan(scanRequest);
            
            ScanIterable scanResponses = client.scanPaginator(scanRequest);
            
            // 結果を表示
            int count = 0;
            int page = 0;
            List <Map <String, AttributeValue>> items;
            Iterator <Map <String, AttributeValue>> map_it;
            Map <String, AttributeValue> currentmap = null;
            Set <String> mapKeys;
            Iterator <String> keys_it;
            String currentkey;
            for (ScanResponse response: scanResponses) {
                page++;
                System.out.format("----- Page %d ----\n",page);
                count += response.count();
                items = response.items();
                map_it = items.iterator();
                while (map_it.hasNext()) {
                    currentmap = map_it.next();
                    mapKeys = currentmap.keySet();
                    keys_it = mapKeys.iterator();
                    while (keys_it.hasNext()) {
                        currentkey = keys_it.next();
                        if (currentkey.equals("gameId")) // my results will have string type gameId
                            System.out.println(currentkey + "=" + currentmap.get(currentkey).s());
                        else                         // the rest are number
                            System.out.println(currentkey + "=" + currentmap.get(currentkey).n());
                    }
                }
                
            }
            System.out.println("count=" + count);
            
            //scanResponses.items().forEach(item -> System.out.format("userId=%s, gameId = %s, score=%s, life=%s\n", item.get("userId").n(),item.get("gameId").s(),item.get("score").n(),item.get("life").n()));


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

