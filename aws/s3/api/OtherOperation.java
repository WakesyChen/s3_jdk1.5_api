package aws.s3.api;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;

import aws.s3.api.ObjectOperation;

public class OtherOperation extends BaseOperation {
	
	public static String generate_presigned_url(String bucketname, String objectname) {
		String url_str = "";
		try {
			java.util.Date expiration = new java.util.Date();
			long expTimeMillis = expiration.getTime();
	        expTimeMillis += 1000 * 3600 * 24 * 1; 
	        expiration.setTime(expTimeMillis);
	        
	        GeneratePresignedUrlRequest generatePresignedUrlRequest = 
	                new GeneratePresignedUrlRequest(bucketname, objectname)
	                .withMethod(HttpMethod.GET)
	                // .withContentType("application/x-www-form-urlencoded")
	                .withExpiration(expiration);
	        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
	        
	        System.out.println("presigned url is:");
	        System.out.println(url.toString());
	        url_str = url.toString();
	        
		}catch (AmazonServiceException ase) {
			System.out.println("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			System.out.println("s3_clt_error_message:" + ace.getMessage());
		}
		return url_str;
	}
	
	/*递归上传某个目录的文件*/
	public static Boolean uploadDir(String start_path) {
		File current_file = new File(start_path);
		// 首先校验是否是目录
		if( ! current_file.isDirectory() ) {  
			System.out.println("filepath is not a  directory!");
			return false;
		}
		// 开始遍历当前目录
		for(File file : current_file.listFiles()) {  
			String current_path =file.getAbsolutePath();
			if(file.isFile()) { 
				System.out.println(current_path);
				String object_key = current_path.substring(upload_dir.length()).replace("\\", "/");
				if(object_key.startsWith("/")) {
					object_key = object_key.substring(1);
				}
				ObjectOperation.putObject(bucket, object_key, current_path);  
			}else {
				uploadDir(current_path);
			}
		}
		return true;			
   }
	
	public static void cancelObjectACL(String bucketname, String objectname, String userid, Permission permission) {
		try {
			AccessControlList acl = s3client.getObjectAcl(bucketname, objectname);
			Owner owner = acl.getOwner();
			// acl.getGrantsAsList().clear();
			Grant grant1 = new Grant(new CanonicalGrantee(userid), permission);
//			Predicate<Grant> predicate1 = grant->grant.equals(grant1);
			AccessControlList new_acl = new AccessControlList();
			if (acl.getGrantsAsList() != null){
				List<Grant> new_grants = acl.getGrantsAsList();
				for(Grant grant : new_grants) {
//					if(!predicate1.test(grant)) {
						new_acl.getGrantsAsList().add(grant);
//					}
				}
			}
			new_acl.setOwner(owner);
			s3client.setObjectAcl(bucketname, objectname, new_acl);
		} catch (AmazonServiceException e) {
				log.error(e.getErrorMessage());
		        
			}
	}
	
	private static void add_grant_with_id(Collection<Grant> grantCollection, String userid, Permission permission) {
		Grant grant = new Grant(new CanonicalGrantee(userid), permission);
		grantCollection.add(grant);
	}
	
	public static void main(String[] args) {
//		Collection<Grant> grantCollection = new ArrayList<Grant>();
//		add_grant_with_id(grantCollection, s3client.getS3AccountOwner().getId(), Permission.FullControl);
//		add_grant_with_id(grantCollection, "d97790b08b46889cf4aa0fc0e172dfe1", Permission.Read);

	}

}
