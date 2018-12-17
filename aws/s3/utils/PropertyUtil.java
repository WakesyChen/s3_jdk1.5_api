package aws.s3.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


public class PropertyUtil {
	/*
	 * 获取配置文件中的属性
	 */
	
	 public final static String configPath= System.getProperty("user.dir") + "\\config\\user.properties";
	 
	 public static String getProperty(String keyName) {
		   	Properties properties = new Properties();
		   	InputStreamReader inputStreamReader;
		   	String value = "";
			try {
//				inputStreamReader = new InputStreamReader(new FileInputStream(configPath), "utf-8"); // jdk1.5b不能这样指定编码, 中文解析会乱码
				InputStream inputStream = new FileInputStream(configPath); 
				properties.load(inputStream);
				value =  properties.getProperty(keyName, "");
				System.out.println(keyName + " : " + value);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return value;
	 }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ep = PropertyUtil.getProperty("endpoint");
		System.out.println("endpoint is :" + ep);
	}

}
