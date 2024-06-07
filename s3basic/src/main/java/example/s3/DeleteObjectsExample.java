package example.s3;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.io.File;
import java.util.ArrayList;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class DeleteObjectsExample {
    public static void main(String[] args)  {
        S3Client client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String bucketName = readProperties().getProperty("bucket_name");
            String aws_region = readProperties().getProperty("aws_region");
            String objectKey = readProperties().getProperty("objectKey");
            Region labRegion = Region.of(aws_region);
    
            // クライアントの作成
            client = S3Client.builder()
                    .region(labRegion)
                    .build();
    
            System.out.format("\nDelete object: %s from %s\n\n", objectKey,bucketName);
            
            // 削除するオブジェクトのキーを ArrayList に追加 
            ArrayList<ObjectIdentifier> keys = new ArrayList<>();
            
            ObjectIdentifier objectId = ObjectIdentifier.builder()
                    .key(objectKey)
                    .build();
            
            keys.add(objectId);
            
            // Delete の作成
            Delete del = Delete.builder()
                .objects(keys)
                .build();

            // DeleteObjectsRequest 作成 
            DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(del)
                    .build();
                    
            // DeleteObjects 実行(複数のオブジェクトを削除可能)
            client.deleteObjects(multiObjectDeleteRequest);
            
            System.out.println("Multiple objects are deleted!");
         } 
        catch (S3Exception s3e) {
            System.err.println(s3e.awsErrorDetails().errorMessage());
            System.exit(1);
        } 
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }  
        finally {
            client.close();  // クライアントのクローズ
        }
    
    }
    
    // config.properties からプロパティを取得するユーティリティメソッド
    public static Properties readProperties() throws Exception {
        InputStream configFile = CreateBucketExample.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try { properties.load(configFile); }
        catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
        catch (IOException ioe) { ioe.printStackTrace(); }
        return properties;
    }
}
