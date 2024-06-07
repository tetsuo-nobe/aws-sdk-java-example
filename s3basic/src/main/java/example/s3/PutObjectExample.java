package example.s3;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.io.File;
import java.nio.file.Paths;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class PutObjectExample {
    public static void main(String[] args)  {
        S3Client client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String bucketName = readProperties().getProperty("bucket_name");
            String aws_region = readProperties().getProperty("aws_region");
            String objectKey = readProperties().getProperty("objectKey");
            String filePath = readProperties().getProperty("put_text_file");
            Region labRegion = Region.of(aws_region);
    
            // クライアントの作成
            client = S3Client.builder()
                    .region(labRegion)
                    .build();
    
            System.out.format("\nPut object: %s into %s\n\n", objectKey,bucketName);
            // PutObjectRequest の作成
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
                    
            // PutObject の実行
            client.putObject(putOb, RequestBody.fromFile(Paths.get(filePath)));
            
            System.out.println("Successfully placed " + objectKey + " into bucket " + bucketName);

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
