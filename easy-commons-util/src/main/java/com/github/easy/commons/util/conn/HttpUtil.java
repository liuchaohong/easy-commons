package com.github.easy.commons.util.conn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 网络连接工具类
 * @author LIUCHAOHONG
 *
 */
public class HttpUtil {
	
	public static String POST = "POST";
	public static String GET = "GET";
	public static String DELETE = "DELETE";
	public static String FormEncodedUTF8 = "application/x-www-form-urlencoded;charset=UTF-8";
	public static String FormJson = "application/json;charset=UTF-8";
	
	/**
	 * 建POST URL立连接，并发送数据
	 * @param url
	 * @param params
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static String urlPOSTRequest(String url, String params) throws ProtocolException, IOException {
		HttpURLConnection c = getUrlConnectionPOST(url, FormEncodedUTF8);
		if (StringUtils.isNotBlank(params)) {
			c.getOutputStream().write(params.getBytes());
			c.getOutputStream().flush();
		}
		return IOUtils.toString(c.getInputStream());
	}
	
	/**
	 * 建POST URL立连接，并发送json数据
	 * @param url
	 * @param params
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static String urlPOSTJsonRequest(String url, String params) throws ProtocolException, IOException {
		HttpURLConnection c = getUrlConnectionPOST(url, FormJson);
		if (StringUtils.isNotBlank(params)) {
			c.getOutputStream().write(params.getBytes());
			c.getOutputStream().flush();
		}
		return IOUtils.toString(c.getInputStream());
	}	
	
	/**
	 * 建立POST URL连接
	 * @param urlStr
	 * @return
	 * @throws IOException
	 * @throws ProtocolException
	 */
	public static HttpURLConnection getUrlConnectionPOST(String url, String contentType) throws IOException, ProtocolException {
		contentType = StringUtils.defaultIfEmpty(contentType, FormEncodedUTF8);
		return getUrlConnection(url, POST, contentType);
	}
	
	/**
	 * 建GET URL立连接，并发送数据
	 * @param url
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static String urlGETRequest(String url) throws ProtocolException, IOException {
		HttpURLConnection c = getUrlConnectionGET(url);
		return IOUtils.toString(c.getInputStream());
	}
	
	/**
	 * 建立GET URL连接
	 * @param urlStr
	 * @return
	 * @throws IOException
	 * @throws ProtocolException
	 */
	public static HttpURLConnection getUrlConnectionGET(String url) throws IOException, ProtocolException {
		return getUrlConnection(url, GET, FormEncodedUTF8);
	}

	/**
	 * 建DELETE URL立连接，并发送数据
	 * @param url
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static String urlDELETERequest(String url) throws ProtocolException, IOException {
		HttpURLConnection c = getUrlConnectionDelete(url);
		return IOUtils.toString(c.getInputStream());
	}
	
	/**
	 * 建立DELETE URL连接
	 * @param urlStr
	 * @return
	 * @throws IOException
	 * @throws ProtocolException
	 */	
	public static HttpURLConnection getUrlConnectionDelete(String url) throws IOException, ProtocolException {
		return getUrlConnection(url, DELETE, FormEncodedUTF8);
	}
	
	/**
	 * 建立URL连接
	 * @param url
	 * @param requestMethod
	 * @param contentType
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws IOException
	 * @throws ProtocolException
	 */
	public static HttpURLConnection getUrlConnection(String url, String requestMethod, String contentType) throws IOException, ProtocolException {
		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setConnectTimeout(1000*60*5);
		httpUrlConnection.setReadTimeout(1000*60*5);
		httpUrlConnection.setRequestMethod(requestMethod); 
        httpUrlConnection.setRequestProperty("Content-type", contentType);
		httpUrlConnection.connect();
		return httpUrlConnection;
	}
	
	/**
	 * 重试请求
	 * @param isAliveMonitorUrl
	 * @param params
	 * @param requestUrl
	 * @param maxRetryNum
	 * @param retrySleepTime
	 * @return
	 */
	public static String request(String isAliveMonitorUrl, String params, String requestUrl, int maxRetryNum, long retrySleepTime) {
		int flag = 0;
		while(flag < maxRetryNum) {
			try {
				return HttpUtil.urlPOSTRequest(requestUrl, params);
			} catch (Throwable e) {
				e.printStackTrace();
				if( targeServiceIsAlive(isAliveMonitorUrl) ) {
					flag ++;
				}
				else {
					try {
						Thread.sleep(retrySleepTime);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return "FAIL";
	}
	
	/**
	 * 检测目标服务是否存活，若返回“200”则表示存活
	 * @param url
	 * @return
	 */
	public static boolean targeServiceIsAlive(String isAliveMonitorUrl) {
		String httpReturnResult = null;
		try {
			httpReturnResult = HttpUtil.urlGETRequest(isAliveMonitorUrl);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(StringUtils.equals("200", httpReturnResult)) {
			return true;
		}
		return false;
	}
	
}
