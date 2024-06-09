package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.io.File;
import java.util.Iterator;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class PutItemExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        JsonParser parser  = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Putting your item to Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
                    
            // JSON ファイルの読み込み
            parser = new JsonFactory().createParser(new File("score_data.json"));
            JsonNode rootNode = new ObjectMapper().readTree(parser);

            // JSON 要素のイテレータの取得
            Iterator<JsonNode> iter = rootNode.iterator();

            ObjectNode currentNode;

            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next();
            
                // Put する Item の準備
                String userId = currentNode.path("userId").asText();
                String gameId = currentNode.path("gameId").asText();
                String score = currentNode.path("score").asText();
                String life = currentNode.path("life").asText();
                
                HashMap<String, AttributeValue> itemValues = new HashMap<>();
                itemValues.put("userId", AttributeValue.builder().n(userId).build());
                itemValues.put("gameId", AttributeValue.builder().s(gameId).build());
                itemValues.put("score", AttributeValue.builder().n(score).build());
                itemValues.put("life", AttributeValue.builder().n(life).build());
    
                // PutItemRequest の作成
                PutItemRequest request = PutItemRequest.builder()
                        .tableName(tableName)
                        .item(itemValues)
                        .build();
                
                // putItem の実行
                client.putItem(request);  
            }
            System.out.format("%s was successfully upsertdated. \n",tableName);
            parser.close();

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

