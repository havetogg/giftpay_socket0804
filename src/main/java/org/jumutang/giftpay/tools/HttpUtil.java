package org.jumutang.giftpay.tools;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * httpClient请求工具�?
 * 
 * @author 鲁雨
 * @since 20161223
 * @version v1.0
 * 
 * copyright Luyu(18994139782@163.com)
 *
 */
public class HttpUtil {

	public final static String UTF8="UTF-8";
	
	/**
	 * 发送get请求
	 * @param url
	 * @param encode
	 * @param id
	 * @return
	 */
	public static String sendGet(String url, String encode,String id) {

		CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(300000).setConnectTimeout(300000).build();//设置请求和传输超时时�?
		get.setConfig(requestConfig);
		get.setHeader("Content-Type", "text/html;charset=UTF-8");
		if(id !=null){
			get.setHeader("identity",id);
		}
		try {
			HttpResponse httpResponse = closeableHttpClient.execute(get);
			HttpEntity responceEntity = httpResponse.getEntity();
			System.out.println("status:" + httpResponse.getStatusLine());
			return EntityUtils.toString(responceEntity, encode);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				closeableHttpClient.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}


	/**
	 * 发送post请求
	 * @param url
	 * @param encode
	 * @param xmlParams
	 * @return
	 */
	public static String sendPost(String url, String encode, String xmlParams,String id) {

		CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时�?
		post.setConfig(requestConfig);
		post.setHeader("Content-Type", "text/html;charset=UTF-8");
		if(id!=null){
			post.setHeader("identity", id);
		}
		try {
			if (xmlParams != null) {
				StringEntity stringEntity = new StringEntity(xmlParams, encode);
				System.out.println("sb=" + xmlParams);
				post.setEntity(stringEntity);
			}
			post.addHeader("Content-Type", "text/xml");
			HttpResponse httpResponse = closeableHttpClient.execute(post);
			HttpEntity responceEntity = httpResponse.getEntity();
			System.out.println("status:" + httpResponse.getStatusLine());
			return EntityUtils.toString(responceEntity, encode);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				closeableHttpClient.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	public static String sendPost(String url, String encode,Map<String,String> param) {
		List<NamedValue> list=new ArrayList<NamedValue>();
		for (String key:param.keySet()){
			list.add(new NamedValue(key,param.get(key)));
		}
		PostOrGet post=new PostOrGet(encode);
		String result=post.sendPost(url, ESendHTTPModel._SEND_MODEL_ENCODE,list);
		return result;
	}
	public static String sendPost(String url, String encode,JSONObject obj) {
		List<NamedValue> list=new ArrayList<NamedValue>();
		for(Object set:obj.keySet()){
			list.add(new NamedValue(String.valueOf(set),obj.getString(String.valueOf(set))));
		}
		PostOrGet post=new PostOrGet(encode);
		String result=post.sendPost(url, ESendHTTPModel._SEND_MODEL_ENCODE,list);
		return result;
	}
	public static String sendPost(String url, String encode, String xmlParams) {

		CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "text/html;charset=UTF-8");
		try {
			if (xmlParams != null) {
				StringEntity stringEntity = new StringEntity(xmlParams, encode);
				System.out.println("sb=" + xmlParams);
				post.setEntity(stringEntity);
			}
			post.addHeader("Content-Type", "text/xml");
			HttpResponse httpResponse = closeableHttpClient.execute(post);
			HttpEntity responceEntity = httpResponse.getEntity();
			System.out.println("status:" + httpResponse.getStatusLine());
			return EntityUtils.toString(responceEntity, encode);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				closeableHttpClient.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
	}
}
