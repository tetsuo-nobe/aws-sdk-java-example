package example.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileNotFoundException;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;


public class HeadBucketExample {
    public static void main(String[] args)  {
        S3Client client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String bucketName = readProperties().getProperty("bucket_name");

            // クライアントの作成
            client = S3Client.builder()
                    .build();
    
            System.out.format("\nChecking if bucket exists: %s\n\n", bucketName);

            // HeadBucketRequest の作成
            HeadBucketRequest request = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // HeadBucket の実行  
            HeadBucketResponse result = client.headBucket(request);

            // レスポンスのステータスコードが 200 の場合は既に存在している
            if (result.sdkHttpResponse().statusCode() == 200) {
                System.out.println("    Bucket is already existing! ");
            }


        } 
        catch (AwsServiceException awsEx) {
            switch (awsEx.statusCode()) {
                case 404:  // HeadBucket で 例外が発生しステータスコードが 404 の場合はバケットが存在していない
                    System.out.println("    No such bucket existing.");
                    break;
                case 400 :
                    System.out.println("    Indicates that you are trying to access a bucket from a different Region than where the bucket exists.");
                    break;
                case 403 :
                    System.out.println("    Permission errors in accessing bucket.");
                    break;
            }
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
        InputStream configFile = HeadBucketExample.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try { properties.load(configFile); }
        catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
        catch (IOException ioe) { ioe.printStackTrace(); }
        return properties;
    }
}
