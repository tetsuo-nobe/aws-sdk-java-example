package example.dynamodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import java.util.List;

public class ListTablesExample {
    public static void main(String[] args) {
        // バケット名とリージョンを config.properties から取得
        DynamoDbClient client = null;
        try {
            String aws_region = readProperties().getProperty("aws_region");
            Region labRegion = Region.of(aws_region);
            System.out.println("Listing your Amazon DynamoDB tables:\n");
            client = DynamoDbClient.builder()
                    .region(labRegion)
                    .build();

            boolean moreTables = true;
            String lastName = null;

            while (moreTables) {
                ListTablesResponse response = null;
                // ListTablesRequest の作成
                if (lastName == null) {
                    ListTablesRequest request = ListTablesRequest.builder().build();
                    response = client.listTables(request);
                } else {
                    ListTablesRequest request = ListTablesRequest.builder()
                            .exclusiveStartTableName(lastName).build();
                    response = client.listTables(request);
                }
                    
                // Table 名の取得と表示
                List<String> tableNames = response.tableNames();
                if (tableNames.size() > 0) {
                    for (String curName : tableNames) {
                        System.out.format("* %s\n", curName);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }
    
                lastName = response.lastEvaluatedTableName();
                if (lastName == null) {
                    moreTables = false;
                }
            }
        } catch (DynamoDbException ddbe) {
            System.out.println("--- DynamoDbException ---");
            System.err.println(ddbe.getMessage());
            System.exit(1);
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
            
        }
        finally {
            client.close();
        }

        System.out.println("\nDone!");
        
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

