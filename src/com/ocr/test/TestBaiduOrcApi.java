package com.ocr.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

public class TestBaiduOrcApi {
	public static void main(String[] args) {
		//百度ocr图片文字识别接口实例
		//参考http://apistore.baidu.com/apiworks/servicedetail/146.html 百度ocr官方文档
		String path = "F:\\picture\\1.jpg";
		String param = "fromdevice=pc&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=2";
		String httpArg2 = "fromdevice=pc&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=1&image=";
		httpArg2 += ImageUtil.ToBase64String(path);
		String url = "http://apis.baidu.com/apistore/idlocr/ocr";
		long start = System.currentTimeMillis();
		System.out.println(sendRequest(url, httpArg2));
		long end = System.currentTimeMillis();
		long t1 = end - start;

		start = System.currentTimeMillis();
		System.out.println(sendRequest(url, param, path));
		end = System.currentTimeMillis();
		long t2 = end - start;
		System.out.println("上传图片：" + t1 + "毫秒");
		System.out.println("图片转base64：" + t2 + "毫秒");

	}

	// 上传图片到服务器，返回图片上出现文字的坐标和文字内容
	public static String sendRequest(String url, String param, String path) {
		OutputStream outputStream = null;
		DataOutputStream out = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;

		StringBuffer textBuf = new StringBuffer();
		StringBuffer fileBuf = new StringBuffer();
		String BOUNDARY = "------WebKitFormBoundary";
		try {
			URL realurl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) realurl
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + BOUNDARY);
			connection.setRequestProperty("apikey",
					"94b8abefac625fe855e876b5478666e2");
			connection.setDoOutput(true);
			connection.connect();
			outputStream = connection.getOutputStream();
			out = new DataOutputStream(outputStream);
			// 写属性
			String[] strArr = param.split("&");
			for (String str : strArr) {
				String[] temp = str.split("=");
				textBuf.append("\r\n").append("--").append(BOUNDARY)
						.append("\r\n");
				textBuf.append("Content-Disposition: form-data; name=\""
						+ temp[0] + "\"\r\n\r\n" + temp[1] + "\r\n");
			}
			out.write(textBuf.toString().getBytes());
			// 写文件
			File file = new File(path);
			String filename = file.getName();
			MagicMatch match = Magic.getMagicMatch(file, false, true);
			String contentType = match.getMimeType();

			fileBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
			fileBuf.append("Content-Disposition: form-data; name=\"" + "image"
					+ "\"; filename=\"" + filename + "\"\r\n");
			fileBuf.append("Content-Type:" + contentType + "\r\n\r\n");

			out.write(fileBuf.toString().getBytes());

			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			// 结束标记
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();
			if (connection.getResponseCode() >= 300) {
				throw new Exception(
						"HTTP Request is not success, Response code is "
								+ connection.getResponseCode());
			}
			inputStream = connection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultBuffer.toString();
	}

	// 将图片转Base64再转urlcode（百度ocr接口官方例子） ，，返回图片上出现文字的坐标和文字内容
	public static String sendRequest(String url, String param) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();

		try {
			URL url2 = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) url2
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 填入apikey到HTTP header
			connection.setRequestProperty("apikey",
					"94b8abefac625fe855e876b5478666e2");
			connection.setDoOutput(true);
			connection.getOutputStream().write(param.getBytes("UTF-8"));
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
