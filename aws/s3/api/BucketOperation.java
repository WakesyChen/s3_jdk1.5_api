package aws.s3.api;

import java.util.List;
import aws.s3.api.BaseOperation;
import org.apache.log4j.Logger;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;


public class BucketOperation extends BaseOperation {
	
	public static Logger log= Logger.getLogger(BucketOperation.class.getName());
		
	
	/*list所有桶*/
	public static void listBuckets() {
		try {
			List<Bucket> buckets = s3client.listBuckets();
			for(Bucket bucket: buckets)
				log.info(bucket.getName());
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	/*创建桶*/
	public static void createBucket(String bucket_name) {
		try {
			s3client.createBucket(bucket_name);
			log.info("create a bucket successfully, " + bucket_name);
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	/*删除桶, 桶中对象必须为空*/
	public static void deleteBucket(String bucket_name) {
		try {
			s3client.deleteBucket(bucket_name);
			log.info("delete a bucket successfully, " + bucket_name);
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bucket_name = "wakesy2";
		BucketOperation bucket_operation = new BucketOperation();
		createBucket(bucket_name);   //有问题
//		deleteBucket(bucket_name);
		listBuckets();
	}

}
