package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableResponse;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;

public class DeleteTableExample {
    public static void main(String[] args) {
        
        DynamoDbClient client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String aws_region = readProperties().getProperty("aws_region");
            String tableName = readProperties().getProperty("table_name");
            
            Region labRegion = Region.of(aws_region);
            System.out.println("Deleting your Amazon DynamoDB tables:\n");
            
            // クライアントの作成
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();
            
            // DynamoDbWaiter の作成
            DynamoDbWaiter dbWaiter = client.waiter();
            
            
            // DeleteTableRequest の作成
            DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(tableName)
                .build();
            
            // deleteTable の実行
            DeleteTableResponse response = client.deleteTable(request);
            
            // DescribeTableRequest の作成
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build();
    
            // Table 作成が完了するまで待機
            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableNotExists(tableRequest);
            // Table 名を出力
            String newTable = response.tableDescription().tableName();
            System.out.format("table is deleted: %s\n",newTable);

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

