package aws.s3.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class MultiOperation extends BaseOperation{
	
	final static int s3_part_size =  1024 * 1024 * 5; //  Each part must be at least 5 MB 
	
	public static void main(String[] args) {
		MultiOperation multiOperation = new MultiOperation();
		// TODO Auto-generated method stub
		String local_file = System.getProperty("user.dir") + "\\test_resource\\360wallpaper_bizhi_home.exe";
		String object_key = "test_resource/360wallpaper_bizhi_home.exe"; 
		InputStream is = null;
		try {
			is = new FileInputStream(new File(local_file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		multiUpload(bucket, object_key, local_file);
//		multiUploadByStream(bucket, object_key, is);
//		listMultipart(bucket);
//		listMultipart(bucket, object_key, "2~JQD1ljkuun6VpbznaMAScKDBJwj2ghV"); // not work after multiUpload finished
		multiUploadRetry(bucket, object_key, local_file, "2~JQD1ljkuun6VpbznaMAScKDBJwj2ghV"); //

	}
	
	/**
	 * list 某次分片上传，成功部分的对象分片；如果已经完整上传，则查询不到
	 * @param bucket_name
	 * @param object_key
	 * @param s3_multi_uploadid
	 */
	public static void listMultipart(String bucket_name, String object_key, String s3_multi_uploadid) {
		try {
			ListPartsRequest list_parts_request = new ListPartsRequest(bucket_name, object_key, s3_multi_uploadid);
			PartListing s3_parts = s3client.listParts(list_parts_request);
			
			for (PartSummary part : s3_parts.getParts()) {
				log.info("PartNumber: " + part.getPartNumber() + " ETag: " + part.getETag());
			}
		} catch (AmazonServiceException ase) {
			log.error("s3_svr_error_message:" + ase.getMessage());
		}
	}

	
	public static void listMultipart(String bucket_name) {
		try {
			ListMultipartUploadsRequest s3_list_req = new ListMultipartUploadsRequest(bucket_name);
			MultipartUploadListing s3_list_res = s3client.listMultipartUploads(s3_list_req);
			for (MultipartUpload multipartUpload : s3_list_res.getMultipartUploads()) {
				log.info("Key: " + multipartUpload.getKey() + " UploadId: " + multipartUpload.getUploadId());
				AbortMultipartUploadRequest s3_abort_req = new AbortMultipartUploadRequest(
								bucket_name, multipartUpload.getKey(),multipartUpload.getUploadId());
				s3client.abortMultipartUpload(s3_abort_req);
			}
		} catch (AmazonServiceException ase) {
			log.error("s3_svr_error_message:" + ase.getMessage());
		} catch (AmazonClientException ace) {
			log.error("s3_clt_error_message:" + ace.getMessage());
		}
	}
	
	
	
	/**
	 * 分片上传本地文件
	 * @param bucket_name
	 * @param object_key
	 * @param file_path
	 * @return 
	 */
	public static String multiUpload(String bucket_name, String object_key, String file_path) {

		InitiateMultipartUploadRequest s3_multi_req = new InitiateMultipartUploadRequest(bucket_name, object_key);
		InitiateMultipartUploadResult s3_multi_res = s3client.initiateMultipartUpload(s3_multi_req);
		String s3_multi_uploadid = s3_multi_res.getUploadId();
		File local_file = new File(file_path);
		int s3_part_count = (int) Math.ceil((double) (local_file.length()) / (double) s3_part_size);
		log.info(" -- part_num " + s3_part_count + " ----id:" + s3_multi_uploadid);
		List<PartETag> s3_part_etags = new java.util.ArrayList<PartETag>();
		try {
			for (int part_no = 0; part_no < s3_part_count; part_no++) {
				FileInputStream s3_input = new FileInputStream(local_file);
				
				long s3_offset_bytes = s3_part_size * part_no;
				s3_input.skip(s3_offset_bytes);

				long part_size = s3_part_size < (local_file.length() - s3_offset_bytes) ? s3_part_size
				: (local_file.length() - s3_offset_bytes);

				UploadPartRequest s3_upload_req = new UploadPartRequest();
				s3_upload_req.setBucketName(bucket_name);
				s3_upload_req.setKey(object_key);
				s3_upload_req.setUploadId(s3_multi_uploadid);
				s3_upload_req.setInputStream(s3_input);
				s3_upload_req.setPartSize(part_size);
				s3_upload_req.setPartNumber(part_no + 1);
				UploadPartResult s3_upload_res = s3client.uploadPart(s3_upload_req);

				s3_part_etags.add(s3_upload_res.getPartETag());
				s3_input.close();
				log.info(" -- part_id" + part_no + " Etag: " + s3_upload_res.getPartETag().getETag());
			}
			PartListing s3_parts = s3client.listParts(new ListPartsRequest(bucket_name, object_key, s3_multi_uploadid));
			for (PartSummary part : s3_parts.getParts()) {
				log.info("PartNumber: " + part.getPartNumber() + " ETag: " + part.getETag());
			}
			CompleteMultipartUploadRequest s3_complete_req = new CompleteMultipartUploadRequest(
										bucket_name, object_key, s3_multi_res.getUploadId(),s3_part_etags);
			CompleteMultipartUploadResult s3_complete_res = s3client.completeMultipartUpload(s3_complete_req);
			log.info(s3_complete_res.getETag());
		} catch (Exception ie) {
			log.error(ie.getMessage());
			ie.printStackTrace();
			s3client.abortMultipartUpload(new AbortMultipartUploadRequest(bucket_name, object_key, s3_multi_uploadid));
		}
		return s3_multi_uploadid;
	}

	/**
	 * 失败后重传，分片大小必须不小于5M，否则会失败
	 * @param bucket_name
	 * @param object_key
	 * @param file_path
	 * @param s3_multi_uploadid
	 */
	public static void multiUploadRetry(String bucket_name, String object_key, String file_path, String s3_multi_uploadid) {
		InitiateMultipartUploadRequest s3_multi_req = new InitiateMultipartUploadRequest(bucket_name, object_key);
		InitiateMultipartUploadResult s3_multi_res = s3client.initiateMultipartUpload(s3_multi_req);
		s3_multi_res.setUploadId(s3_multi_uploadid);
		File local_file = new File(file_path);
		int s3_part_count = (int) Math.ceil((double) (local_file.length()) / (double) s3_part_size);

		List<Integer> parts_uploaded = new java.util.ArrayList<Integer>();
		PartListing s3_parts = s3client.listParts(new ListPartsRequest(bucket_name, object_key, s3_multi_uploadid));
		log.info(" -- part_num " + s3_part_count + " ----id:" + s3_multi_uploadid);
		List<PartETag> s3_part_etags = new java.util.ArrayList<PartETag>();
		for (PartSummary part : s3_parts.getParts()) {
			log.info("allready uploaded: PartNumber: " + part.getPartNumber() + " ETag: " + part.getETag());
			int partNumber = part.getPartNumber();
			parts_uploaded.add(partNumber);
			PartETag part_etag = new PartETag(partNumber, part.getETag());
			s3_part_etags.add(part_etag);
		}
		
		try {
			for (int part_no = 0; part_no < s3_part_count; part_no++) {
				
				if(parts_uploaded.contains(part_no + 1)) {
					continue;
				}
				FileInputStream s3_input = new FileInputStream(local_file);
				long s3_offset_bytes = s3_part_size * part_no;
				s3_input.skip(s3_offset_bytes);
				long part_size = s3_part_size < (local_file.length() - s3_offset_bytes) ? s3_part_size
				: (local_file.length() - s3_offset_bytes);

				UploadPartRequest s3_upload_req = new UploadPartRequest();
				s3_upload_req.setBucketName(bucket_name);
				s3_upload_req.setKey(object_key);
				s3_upload_req.setUploadId(s3_multi_uploadid);
				s3_upload_req.setInputStream(s3_input);
				s3_upload_req.setPartSize(part_size);
				s3_upload_req.setPartNumber(part_no + 1);
				UploadPartResult s3_upload_res = s3client.uploadPart(s3_upload_req);

				s3_part_etags.add(s3_upload_res.getPartETag());
				s3_input.close();
				log.info(" -- part_id" + part_no + " Etag: " + s3_upload_res.getPartETag().getETag());
			}
			CompleteMultipartUploadRequest s3_complete_req = new CompleteMultipartUploadRequest(
			bucket_name, object_key, s3_multi_uploadid, s3_part_etags);
			CompleteMultipartUploadResult s3_complete_res = s3client.completeMultipartUpload(s3_complete_req);
			log.info(s3_complete_res.getETag());
		} catch (Exception ie) {
			log.error(ie.getMessage());
			ie.printStackTrace();
			s3client.abortMultipartUpload(new AbortMultipartUploadRequest(bucket_name, object_key, s3_multi_uploadid));

		}
	}

	/**
	 * 分片上传数据流
	 * @param bucket_name
	 * @param object_key
	 * @param inputStream
	 */
	public static void multiUploadByStream(String bucket_name,String object_key, InputStream inputStream) {
		
		InitiateMultipartUploadRequest s3_multi_req = new InitiateMultipartUploadRequest(bucket_name, object_key);
		InitiateMultipartUploadResult s3_multi_res = s3client.initiateMultipartUpload(s3_multi_req);
		String s3_multi_uploadid = s3_multi_res.getUploadId();
		List<PartETag> s3_part_etags = new java.util.ArrayList<PartETag>();
		try {
			byte[] bs =new byte[s3_part_size];
			long part_size = 0;
			for (int part_no = 0; (part_size = inputStream.read(bs)) != -1; part_no++) {
				UploadPartRequest s3_upload_req = new UploadPartRequest();
				s3_upload_req.setBucketName(bucket_name);
				s3_upload_req.setKey(object_key);
				s3_upload_req.setUploadId(s3_multi_uploadid);
				s3_upload_req.setInputStream( new ByteArrayInputStream(bs));
				s3_upload_req.setPartSize(part_size);
				s3_upload_req.setPartNumber(part_no + 1);
				UploadPartResult s3_upload_res = s3client.uploadPart(s3_upload_req);
				s3_part_etags.add(s3_upload_res.getPartETag());
				log.info(" -- part_id" + part_no + " Etag: " + s3_upload_res.getPartETag().getETag());
			}
			CompleteMultipartUploadRequest s3_complete_req = new CompleteMultipartUploadRequest(
					bucket_name, object_key, s3_multi_res.getUploadId(),s3_part_etags);
			CompleteMultipartUploadResult s3_complete_res = s3client.completeMultipartUpload(s3_complete_req);
			log.info(s3_complete_res.getETag());
		} catch (Exception ie) {
			log.error(ie.getMessage());
			ie.printStackTrace();
			s3client.abortMultipartUpload(new AbortMultipartUploadRequest(bucket_name, object_key, s3_multi_uploadid));
		}
	}
	

	
}
