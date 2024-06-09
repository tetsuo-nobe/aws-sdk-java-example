package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


public class UpdateItemExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Deleting your item from Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
            
            // Update する Item のキーの準備
            String userId = "3";
            String gameId = "G001";
            
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("userId", AttributeValue.builder().n(userId).build());
            itemValues.put("gameId", AttributeValue.builder().s(gameId).build());
            
            // Define the update expression
            String updateExpression = "SET life = life + :addLife";
            
            // Define the values for the update expression
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
            expressionAttributeValues.put(":addLife", AttributeValue.builder().n("1").build());
            
            // Define the condition expression
            String conditionExpression = "score > :threshold";
            
            // Define the values for the condition expression
            expressionAttributeValues.put(":threshold", AttributeValue.builder().n("3000").build());
            
            // Create the update request
            UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemValues)
                .updateExpression(updateExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .conditionExpression(conditionExpression)
                .build();

            // Send the request to update the item
            UpdateItemResponse response = client.updateItem(request);

            System.out.format("%s was successfully updated. The request id is %s\n",tableName , response.responseMetadata().requestId());


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

