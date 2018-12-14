package aws.s3.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import com.amazonaws.AmazonServiceException;


public class CommonUtils {
	
	
	/*用于将数据流，写到本地文件*/
	public static void saveFileByFIS(InputStream is, String target_file) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(target_file));
			byte[] read_buf = new byte[1024];
			int read_len = 0;
			while((read_len = is.read(read_buf)) > 0) {
				fos.write(read_buf, 0, read_len);
			}
			is.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (AmazonServiceException e) {
            e.printStackTrace();
        }

	}
	
	
	/*默认log4j,properties 日志配置文件，需要放在src目录下，用此方法重新指定路径*/
	public static void initLog4jConfig() {
		Properties props = null;
		FileInputStream fis = null;
		try {
			// 从配置文件dbinfo.properties中读取配置信息
			props = new Properties();
			fis = new FileInputStream("config/log4j.properties");
			props.load(fis);
			PropertyConfigurator.configure(props);//装入log4j配置信息
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			fis = null;
		}
	}

	
	public static void test() {
		try {
			final String configPath= System.getProperty("user.dir") + "\\config\\user.properties";
			final String configPath2= System.getProperty("user.dir") + "\\config\\user.properties2";
			FileInputStream fis = new FileInputStream(new File(configPath));
			saveFileByFIS(fis, configPath2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		test();

	}

}
