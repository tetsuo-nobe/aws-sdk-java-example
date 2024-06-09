package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileNotFoundException;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

public class CreateTableExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            String partitionKey = readProperties().getProperty("partition_key");
            String sortKey = readProperties().getProperty("sort_key");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Creating your Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
            
            // DynamoDbWaiter の作成
            DynamoDbWaiter dbWaiter = client.waiter();
            
            // キー属性の定義   
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();

            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName(partitionKey)
                    .attributeType("N")
                    .build());
    
            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName(sortKey)
                    .attributeType("S")
                    .build());
                    
            ArrayList<KeySchemaElement> tableKey = new ArrayList<>();
            
            KeySchemaElement pkey = KeySchemaElement.builder()
                    .attributeName(partitionKey)
                    .keyType(KeyType.HASH)
                    .build();
    
            KeySchemaElement skey = KeySchemaElement.builder()
                    .attributeName(sortKey)
                    .keyType(KeyType.RANGE)
                    .build();
            
            tableKey.add(pkey);
            tableKey.add(skey);
            
            // CreateTableRequest の作成
            CreateTableRequest request = CreateTableRequest.builder()
                    .attributeDefinitions(attributeDefinitions)
                    .keySchema(tableKey)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(1L)
                            .writeCapacityUnits(1L)
                            .build())
                    .tableName(tableName)
                    .build();
                    
            // createTable の実行
            CreateTableResponse response = client.createTable(request);
            
            // DescribeTableRequest の作成
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build();
    
            // Table 作成が完了するまで待機
            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            // Table 名を出力
            String newTable = response.tableDescription().tableName();
            System.out.format("New table is %s\n",newTable);

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

