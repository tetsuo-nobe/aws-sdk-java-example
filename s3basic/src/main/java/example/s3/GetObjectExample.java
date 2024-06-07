package example.s3;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.io.File;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class GetObjectExample {
    public static void main(String[] args)  {
        S3Client client = null;
        try {
            // バケット名とリージョンを config.properties から取得
            String bucketName = readProperties().getProperty("bucket_name");
            String aws_region = readProperties().getProperty("aws_region");
            String objectKey = readProperties().getProperty("objectKey");
            String filePath = readProperties().getProperty("get_text_file");
            Region labRegion = Region.of(aws_region);
    
            // クライアントの作成
            client = S3Client.builder()
                    .region(labRegion)
                    .build();
    
            System.out.format("\nGet object: %s from %s\n\n", objectKey,bucketName);

            // GetObjectRequest の作成
            GetObjectRequest getOb = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // GetObject 実行
            ResponseBytes<GetObjectResponse> objectBytes = client.getObjectAsBytes(getOb);
            byte[] data = objectBytes.asByteArray();

            // ローカルファイルとして書き込み
            File myFile = new File(filePath);
            OutputStream os = new FileOutputStream(myFile);
            os.write(data);
            System.out.println("Successfully obtained bytes from an S3 object");
            os.close();
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
