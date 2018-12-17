/**
 *  本项目是修改 AWS-S3-SDK 源码
 *  jdk1.5 及以上均可放心使用
 */

package aws.s3.api;

import org.apache.log4j.Logger;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

import aws.s3.utils.CommonUtils;
import aws.s3.utils.PropertyUtil;

public class BaseOperation {

	public static String endpoint ;
	public static String access_key ;
	public static String secret_key ;
	public static String bucket ;
	public static String upload_dir ;
	public static AmazonS3 s3client ;
	public static Logger log= Logger.getLogger(BaseOperation.class.getName());
	
	public BaseOperation() {
		this.access_key = PropertyUtil.getProperty("access_key");
		this.secret_key = PropertyUtil.getProperty("secret_key");
		this.endpoint = PropertyUtil.getProperty("endpoint");
		this.bucket = PropertyUtil.getProperty("bucket");
		this.upload_dir = PropertyUtil.getProperty("upload_dir");
		CommonUtils.initLog4jConfig(); 
		initS3Connection();   
	}
	

	public static void initS3Connection() {
		try {
			if(s3client  == null) {
				AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
				ClientConfiguration connconfig = new ClientConfiguration();
	            connconfig.setProtocol(Protocol.HTTP);
	            connconfig.setSignerOverride("S3SignerType");
	            s3client = new  AmazonS3Client(credentials, connconfig);
	            s3client.setEndpoint(endpoint);
	            log.info("Create s3 connection successfully!");
	            s3client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());
			}
		}catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BaseOperation bop = new BaseOperation();
		initS3Connection();
	}
		
	
}
