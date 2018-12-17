package aws.s3.api;

import java.util.List;
import aws.s3.api.BaseOperation;
import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.VersionListing;


public class BucketOperation extends BaseOperation {
	
	public static Logger log= Logger.getLogger(BucketOperation.class.getName());
		
	
	/**
	 * list 所有桶
	 */
	public static void listBuckets() {
		try {
			List<Bucket> buckets = s3client.listBuckets();
			for(Bucket bucket: buckets)
				log.info(bucket.getName());
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建桶
	 * @param bucket_name
	 */
	public static void createBucket(String bucket_name) {
		try {
			s3client.createBucket(bucket_name);
			log.info("create a bucket successfully, " + bucket_name);
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  删除桶, 桶中对象必须为空
	 * @param bucket_name
	 */
	public static void deleteBucket(String bucket_name) {
		try {
			s3client.deleteBucket(bucket_name);
			log.info("delete a bucket successfully, " + bucket_name);
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * list桶多版本
	 * @param bucket_name
	 */
	public static void listBucketVersion(String bucket_name) {
		try {
			log.info("Listing Bucket Version");
			ListVersionsRequest request = new ListVersionsRequest();
			request.withBucketName(bucket_name);
			VersionListing versionListing;            
			do {
				versionListing = s3client.listVersions(request);
				for (S3VersionSummary objectSummary : versionListing.getVersionSummaries()) {
					log.info(" - " + objectSummary.getKey() + "  " +
					"(size = " + objectSummary.getSize() + ")" +
					"(versionID= " + objectSummary.getVersionId() + ")");
				}
				request.setKeyMarker(versionListing.getNextKeyMarker());
				request.setVersionIdMarker(versionListing.getNextVersionIdMarker());
			} while (versionListing.isTruncated());
		} catch (AmazonServiceException ase) {
			log.error("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.error("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	
	/**
	 * 设置桶多版本
	 * @param bucketName
	 */
	public static void setBucketVersion(String bucket_name) {
		try {
			BucketVersioningConfiguration configuration = new BucketVersioningConfiguration().withStatus("Enabled");
			SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = 
					new SetBucketVersioningConfigurationRequest(bucket_name,configuration);
			s3client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
			BucketVersioningConfiguration conf = s3client.getBucketVersioningConfiguration(bucket_name);
			log.info("bucket versioning configuration status:    " + conf.getStatus());
		} catch (AmazonServiceException ase) {
			log.error("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.error("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	/**
	 * 获取桶权限-ACL
	 * @param bucketname
	 * @param objectname
	 */
	public static void getObjectACL(String bucket_name) {
		try {
			AccessControlList buceket_acl = s3client.getBucketAcl(bucket_name);
			log.info("owner:" + buceket_acl.getOwner().getDisplayName() + ", id: " + buceket_acl.getOwner().getId());
			for (Grant grant : buceket_acl.getGrants()) {
				log.info(grant.getPermission().getHeaderName());
			}
		} catch (AmazonServiceException ase) {
			log.error("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.error("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	/**
	 * 设置桶权限-ACL
	 * @param bucketname
	 * @param objectname
	 */
	public static void setBucketACL(String bucket_name, CannedAccessControlList acl) {
		try {
			s3client.setBucketAcl(bucket_name, acl);
		} catch (AmazonServiceException ase) {
			log.info("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.info("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	public static void main(String[] args) {
		String bucket_name = "wakesy";
		BucketOperation bucketOperation = new BucketOperation();
//		createBucket(bucket_name);   
//		deleteBucket(bucket_name);
		listBuckets();
		setBucketVersion(bucket_name);
		listBucketVersion(bucket_name);
		setBucketACL(bucket_name, CannedAccessControlList.PublicReadWrite);
		getObjectACL(bucket_name);
	}

	
}
