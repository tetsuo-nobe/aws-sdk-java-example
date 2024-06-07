package example.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class CreateBucketExample {
    public static void main(String[] args)  {
        S3Client client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String bucketName = readProperties().getProperty("bucket_name");
            String aws_region = readProperties().getProperty("aws_region");
            Region labRegion = Region.of(aws_region);
    
            // クライアントの作成
            client = S3Client.builder()
                    .region(labRegion)
                    .build();
    
            System.out.format("\nCreating bucket: %s\n\n", bucketName);

            // S3Waiter の取得
            S3Waiter s3Waiter = client.waiter();
            
            // CreateBucketRequest の作成
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // バケット作成
            client.createBucket(bucketRequest);

            // bucketRequestWait の作成
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            System.out.format("Waiting on ");
            
            // バケット作成が完了するまで待機
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.format("    Bucket \"%s\" is ready\n",bucketName);

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
