package com.ocr.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sun.misc.BASE64Encoder;

public class ImageUtil {
	private String path;
	
	//��ͼƬתbase64��ʽ
	public static String ToBase64String(String path){
		byte[] data = null;
		InputStream  in = null;
		String result;
		try {
			in = new FileInputStream(path);
			data = new byte[in.available()];
			in.read(data);
			in.close();
			BASE64Encoder encoder = new BASE64Encoder();
			//TODO �ж���windows��������linux����
			result = encoder.encode(data).replaceAll("\r\n", "");
			return ToUnicodeString(result,"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}
	//תurlencode
	public static String ToUnicodeString(String str,String code) throws UnsupportedEncodingException{
		return URLEncoder.encode(str, code);
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
