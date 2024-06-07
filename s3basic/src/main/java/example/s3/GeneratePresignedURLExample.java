package example.s3;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.time.Duration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class GeneratePresignedURLExample {
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
    
            System.out.format("\nGenerate PresignedURL: %s from %s\n\n", objectKey,bucketName);

            // S3Presigner の作成
            S3Presigner presigner = S3Presigner.create();
            
            // GetObjectPresignRequest の作成
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            
            // GetObjectPresignRequest の作成
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(2))  // The URL will expire in 2 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            // presignGetObject 実行
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            System.out.format("Presigned URL: %s\n", presignedRequest.url().toString());
            System.out.format("HTTP method: %s\n", presignedRequest.httpRequest().method());

            presigner.close();
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
