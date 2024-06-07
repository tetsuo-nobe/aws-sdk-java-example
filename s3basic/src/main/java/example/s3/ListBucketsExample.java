package example.s3;

import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLocationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLocationResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;


public class ListBucketsExample {
    public static void main(String[] args)  {
        S3Client client = null;
        try {

            // クライアントの作成
            client = S3Client.builder()
                    .build();
    
            System.out.format("\nList buckets: \n\n");
            
            // バケットの一覧表示
            ListBucketsResponse response = client.listBuckets();
            List<Bucket> bucketList = response.buckets();
            String bucketName = null;
            GetBucketLocationResponse res = null;
            for (Bucket bucket: bucketList) {
               bucketName = bucket.name();
               // バケットのリージョンを取得 (us-east-1の場合 は空文字になる)
               res = client.getBucketLocation(GetBucketLocationRequest.builder().bucket(bucketName).build());
               // 出力
               System.out.format("%s (%s) \n",bucketName,res.locationConstraintAsString());
            }

 
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
    

}
