package aws.s3.api;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;

import aws.s3.utils.CommonUtils;



public class ObjectOperation extends BaseOperation {

	public static Logger log= Logger.getLogger(ObjectOperation.class.getName());
	
	public static void main(String[] args) {
		String local_file = System.getProperty("user.dir") + "\\test_resource\\common.log";
		String object_key = "test_resource/common.log";   // ====warning==== object_key can't start with '/' 
		String object_key2 = "test_resource/360wallpaper_bizhi_home.exe"; 
		String target_file = local_file + "temp";

		Map<String, String> data_map = new HashMap<String, String>();
		List<Tag> new_tags = new ArrayList<Tag>();
		for(int i =6; i <10; i ++) {
			new_tags.add(new Tag("name"+i, "wakesy"+i));
			data_map.put("name"+i, "wakesy"+i);
		}
		ObjectOperation object_operation =new ObjectOperation();
//		listObjectVersion(bucket, object_key2);
//		setObjectACL(bucket, object_key2, CannedAccessControlList.PublicReadWrite);
		clearObjectACL(bucket, object_key2);
		getObjectACL(bucket, object_key2);
		
//		putObject(bucket, object_key2, local_file); 
//		copyObject(bucket, "test_resource/common.log", "wakesy2", "test_resource/common.log4");
//		getObject(bucket, object_key, target_file);
//		deleteObject(bucket, object_key2);
//		setMetaDataWhenUpload(bucket, object_key, local_file, data_map);
//		getMetaData(bucket, object_key, "name");
		
//		setTags(bucket, object_key, new_tags);  
//		setTagsWhenUpload(bucket, object_key, local_file, new_tags);
//		getTags(bucket, object_key);		

//		listObjects(bucket, "");
	}
	
	
	
	/**
	 * list桶中的对象
	 * @param bucket_name
	 * @param prefix
	 */
	public static void listObjects(String bucket_name, String prefix) {
		try {
			ObjectListing object_list = s3client.listObjects(bucket_name, prefix);
			List<S3ObjectSummary> objects = object_list.getObjectSummaries();
			log.info("listObjects successfully!");
			for(S3ObjectSummary object:objects) {
			    log.info("object: "+ object.getKey());
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传单个文件
	 * @param bucket_name
	 * @param object_key
	 * @param file_path
	 */
	public static void putObject(String bucket_name, String object_key,  String file_path) {
		try {
		   log.debug("start to upload file: " + file_path);
		   PutObjectRequest por = new PutObjectRequest(bucket_name, object_key,  new File(file_path));
		   s3client.putObject(por);
//			   s3client.putObject(bucket_name, object_key, new File(file_path));  // work too
		   log.info("upload object successfully, object_key: " + object_key);
         } catch (AmazonServiceException e) {
        	 e.printStackTrace();
            log.error("upload object failed,  object_key:" + object_key + " error: " + e.getMessage());
        }
	    }
	
	/**
	 * 数据流上传对象
	 * @param bucketname
	 * @param len
	 * @param key
	 * @param inputStream
	 */
	public static void putObjectByStream(String bucketname,long len, String key, InputStream inputStream) {
		try {
			ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(len);
            metadata.setContentType("image/bmp");
			PutObjectRequest s3_req = new PutObjectRequest(bucketname, key, inputStream, metadata);   
			Collection<Grant> grantCollection = new ArrayList<Grant>();
//			add_grant_with_id(grantCollection, s3client.getS3AccountOwner().getId(), Permission.FullControl);
//			add_grant_with_id(grantCollection, "d97790b08b46889cf4aa0fc0e172dfe1", Permission.Read);
			AccessControlList acl = new AccessControlList();
			acl.getGrantsAsList().addAll(grantCollection);
			s3_req.setAccessControlList(acl);

			PutObjectResult s3_putobj_result = s3client.putObject(s3_req);
			log.info("s3_putobj_etag:" + s3_putobj_result.getETag());
		} catch (AmazonServiceException ase) {
			log.info("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.info("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	/**
	 * 下载单个文件
	 * @param bucket_name
	 * @param object_key
	 * @param target_file
	 */
	public static void getObject(String bucket_name, String object_key,  String target_file) {
		// target_file:  对象下载后，对应的本地文件路径
		try {
		   log.debug("start to download object_key: " + object_key);
		   GetObjectRequest get_obj_request = new GetObjectRequest(bucket_name, object_key);
		   S3Object object = s3client.getObject(get_obj_request);
//		   S3Object object =  s3client.getObject(bucket_name, object_key);  // work too
		   InputStream  s3is = object.getObjectContent();
		   CommonUtils.saveFileByFIS(s3is, target_file);
		   log.info("download object successfully, target_file: " + target_file);
         } catch (AmazonServiceException e) {
        	 e.printStackTrace();
            log.error("download object failed,  target_file:" + target_file + " error: " + e.getMessage());
           }
    }

	/**
	 * 复制对象
	 * @param from_bucket
	 * @param from_object
	 * @param to_bucket
	 * @param to_object
	 */
	public static void copyObject( String from_bucket,  String from_object, String to_bucket,  String to_object) {
		try {
		    s3client.copyObject(from_bucket, from_object, to_bucket,  to_object);
		    log.info("copy  object successfully, object_key: " + from_object);
	        } catch (AmazonServiceException e) {
	        	 e.printStackTrace();
	        }
	    }
	
	
	/**
	 * 删除单个文件
	 * @param bucket_name
	 * @param object_key
	 */
	public static void deleteObject(String bucket_name, String object_key) {
		try {
			   log.debug("start to delete object_key: " + object_key);
			   DeleteObjectRequest dor = new DeleteObjectRequest(bucket_name, object_key);
			   s3client.deleteObject(dor);
//			   s3client.deleteObject(bucket_name,  object_key);  // work too
			   log.info("delete object successfully, object_key: " + object_key);
	        } catch (AmazonServiceException e) {
	        	 e.printStackTrace();
	            log.error("delete object failed,  object_key:" + object_key + " error: " + e.getMessage());
	           }
	    }
	
	
	/**
	 *  设置标签--上传后
	 * @param bucket_name
	 * @param object_key
	 * @param new_tags
	 */
	public static void setTags(String bucket_name, String object_key, List<Tag> new_tags) {
		try {
		   SetObjectTaggingRequest sotr = new SetObjectTaggingRequest(bucket_name, object_key, null);
		   // 如果是增量添加，使用以下代码
//		   List<Tag> old_tags = getTags(bucket_name, object_key);
//		   for(Tag old_tag : old_tags) {
//			   Boolean tag_included = false; 
//			   for(Tag new_tag : new_tags) {
//				   if(new_tag.getKey() == old_tag.getKey()) {
//					   tag_included = true;
//				   }
//			   }
//			   if(!tag_included) {
//				   new_tags.add(old_tag);
//				   log.info("include old tag, " + old_tag.getKey() + " : " + old_tag.getValue());
//			   }
//		   }
		   sotr.setTagging(new ObjectTagging(new_tags));
		   s3client.setObjectTagging(sotr);
		   log.info("set new tag successfully!");
			} catch (AmazonServiceException e) {
        	 e.printStackTrace();
           }
	}
	
	
	/**
	 * 设置标签--上传时
	 * @param bucket_name
	 * @param object_key
	 * @param file_path
	 * @param new_tags
	 */
	public static void setTagsWhenUpload(String bucket_name, String object_key, String file_path,  List<Tag> new_tags) {
		try {
			PutObjectRequest por = new PutObjectRequest(bucket_name, object_key,  new File(file_path));
			por.setTagging(new ObjectTagging(new_tags));
			s3client.putObject(por);
		   log.info("set new tag successfully!");
			} catch (AmazonServiceException e) {
        	 e.printStackTrace();
           }
	}
	
	
	/* 获取标签*/
	public static  List<Tag> getTags(String bucket_name, String object_key) {
		 List<Tag> tags = null;
		try {
		   GetObjectTaggingRequest gotr = new GetObjectTaggingRequest(bucket_name, object_key);
		   GetObjectTaggingResult result =  s3client.getObjectTagging(gotr);
		   tags =  result.getTagSet();
		   for(Tag tag : tags) {
			  log.info("get tag> " +tag.getKey()+ " : " + tag.getValue());
		   }
        } catch (AmazonServiceException e) {
        	 e.printStackTrace();
           }
		return tags;
	}
	
	
	/**
	 * 设置元数据
	 * @param bucket_name
	 * @param object_key
	 * @param file_path
	 * @param data_map
	 */
	public static void setMetaDataWhenUpload(String bucket_name, String object_key, String file_path, Map<String, String> data_map) {
		try {
			ObjectMetadata metadata =new ObjectMetadata();
			// 无法单独设置对象元数据，只能在上传的时候设置
			for(String key : data_map.keySet()) {
				metadata.addUserMetadata(key,  data_map.get(key));
			}
			PutObjectRequest por = new PutObjectRequest(bucket_name, object_key,  new File(file_path)).withMetadata(metadata);
			s3client.putObject(por);
			log.info("set metadata successfully");
		}catch(AmazonServiceException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 获取元数据
	 * @param bucket_name
	 * @param object_key
	 * @param meta_key
	 * @return
	 */
	public static Object getMetaData(String bucket_name, String object_key, String meta_key) {
		Object meta_value = null;
		try {
			GetObjectMetadataRequest gmr = new GetObjectMetadataRequest(bucket_name, object_key) ;
			ObjectMetadata metadata  = s3client.getObjectMetadata(gmr);
			Map<String, String> map = metadata.getUserMetadata();
			Iterator iterator = map.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry entry = (Entry) iterator.next(); 
				log.info(entry.getKey() + ":" + entry.getValue());
			}

			log.debug("get metadata " + meta_key + "  :  " + meta_value);
		}catch(AmazonServiceException e) {
			e.printStackTrace();
		}
		return meta_value;
	}
	
	
	/**
	 * list 对象多版本
	 * @param bucket_name
	 * @param object_key
	 */
	public static void listObjectVersion(String bucket_name, String object_key) {
		try {
			log.info("Listing objects Version");
			ListVersionsRequest request = new ListVersionsRequest();
			request.withBucketName(bucket_name);
			request.withPrefix(object_key);
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
	 * 获取对象权限-ACL
	 * @param bucketname
	 * @param objectname
	 */
	public static void getObjectACL(String bucket_name, String object_key) {
		try {
			AccessControlList s3_obj_acl = s3client.getObjectAcl(bucket_name, object_key);
			log.info("owner:" + s3_obj_acl.getOwner().getDisplayName() + ", id: " + s3_obj_acl.getOwner().getId());
			for (Grant grant : s3_obj_acl.getGrants()) {
				log.info(grant.getPermission().getHeaderName());
			}
		} catch (AmazonServiceException ase) {
			log.error("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.error("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	/**
	 * 设置对象权限-ACL
	 * @param bucketname
	 * @param objectname
	 */
	public static void setObjectACL(String bucketname, String objectname, CannedAccessControlList acl) {
		try {
			s3client.setObjectAcl(bucketname, objectname,acl);
		} catch (AmazonServiceException ase) {
			log.info("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.info("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	/**
	 * 清除对象权限-ACL
	 * @param bucket_name
	 * @param object_key
	 */
	public static void clearObjectACL(String bucket_name, String object_key) {
		try {
			AccessControlList acl = s3client.getObjectAcl(bucket_name, object_key);
			Owner owner = acl.getOwner();
			acl.getGrantsAsList().clear();
			acl.grantPermission(new CanonicalGrantee(acl.getOwner().getId()), Permission.FullControl);
			s3client.setObjectAcl(bucket_name, object_key, acl);
		} catch (AmazonServiceException e) {
			log.error(e.getErrorMessage());
			
		}
	}
	

	

}
