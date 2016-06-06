package com.github.easy.commons.util.conn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	
	private static final Log logger = LogFactory.getLog("HttpClientUtil");
	
	public final static String DIRECT_BODY_PARAM = "@DIRECT_BODY_PARAM";

	public static String doGet(String url){
		return doGet(url, null);
	}
	
	public static String doGet(String url, String charset){
		return doExecute(url, null, charset, null);
	}

	public static String doPost(String url, Map<String,Object> map){
		return doPostOrPut(url, map, null, null);
	}
	
	public static String doPostBody(String url,String body,String charset){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair bodyPari = new BasicNameValuePair(DIRECT_BODY_PARAM,body);
		params.add(bodyPari);
		return doExecute(url, params, charset, null);
	}

	public static String doPut(String url, Map<String,Object> map){
		return doPostOrPut(url, map, null, "PUT");
	}
	
	public static String doDelete(String url){
		return doDelete(url, null);
	}
	
	public static String doDelete(String url, String charset){
		return doExecute(url, null, charset, "DELETE");
	}
	
	public static String doPostOrPut(String url, Map<String,Object> map, String charset, String method){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Map.Entry<String, Object> entry:map.entrySet()){
			NameValuePair nameValuePair = null;
			if(entry.getValue() != null){
				nameValuePair=new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
			}else{
				nameValuePair=new BasicNameValuePair(entry.getKey(), "");
			}
			params.add(nameValuePair);
		}
		return doExecute(url, params, charset, method);
	}	
	
	/**
	 * 执行HTTP请求，支持SSL和POST请求
	 * @param url 请求的URL
	 * @param params 如果不为空，则执行POST请求，如果为空，执行GET请求
	 * @param charset 返回的内容的编码，默认使用UTF-8
	 * @param method TODO
	 * @param isSSL 是否使用SSL
	 * @return
	 * @throws Exception
	 */
	public static String doExecute(String url, List<NameValuePair> params, String charset, String method){
		String result = null;
		if(StringUtils.isBlank(url)){
			return result;
		}
		boolean isSSL = false;
		//说明是SSL
		if(url.toLowerCase().startsWith("https")){
			isSSL = true;
		}
		if(StringUtils.isBlank(charset)){
			charset = "UTF-8";
		}
		CloseableHttpClient httpclient = null;
		HttpClientBuilder httpClientBuilder =HttpClients.custom();
		try {
			if(isSSL){
				// Trust own CA and all self-signed certs
				SSLContext sslcontext = SSLContexts.custom().build();
				// Allow TLSv1 protocol only
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslcontext, new String[] { "TLSv1" }, null,
						SSLConnectionSocketFactory.getDefaultHostnameVerifier());
				httpClientBuilder.setSSLSocketFactory(sslsf);
			}
			httpclient=httpClientBuilder.build();
			HttpUriRequest httpUriRequest = null;
			if(CollectionUtils.isNotEmpty(params)){
				HttpEntityEnclosingRequestBase HttpEntity = null;
				if("PUT".equals(method)){
					HttpEntity = new HttpPut(url);
				}else{
					HttpEntity = new HttpPost(url);
				}
				//如果是一个参数，并且代表直接body传输，则直接请求body
				if(params.size() ==1 && DIRECT_BODY_PARAM.equals(params.get(0).getName())){
					HttpEntity.setEntity(new StringEntity(params.get(0).getValue(), charset));
				}else{
					HttpEntity.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				}
				httpUriRequest = HttpEntity;
			}else{
				HttpRequestBase httpRequest = null;
				if("DELETE".equals(method)){
					httpRequest = new HttpDelete(url);
				}else{
					httpRequest = new HttpGet(url);
				}
				httpUriRequest = httpRequest;
			}
			logger.info("Executing request " + httpUriRequest.getRequestLine());
			
			CloseableHttpResponse response = httpclient.execute(httpUriRequest);
			try {
				HttpEntity entity = response.getEntity();
				result=IOUtils.toString(entity.getContent(), charset);
				logger.info(result);
				EntityUtils.consume(entity);
			} catch (Exception e) {
				logger.info("response.getEntity", e);
			}finally {
				response.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("http execute error,url:" + url + " params:" + params, e);
		} finally {
			try {
				if(httpclient!=null){
					httpclient.close();
				}
			} catch (Exception e) {
				logger.info("httpclient.close", e);
			}
		}
		return result;
	}
	
}
