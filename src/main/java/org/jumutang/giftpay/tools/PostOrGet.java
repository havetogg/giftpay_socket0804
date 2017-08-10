package org.jumutang.giftpay.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 发送http请求
 * 
 * @author YuanYu
 *
 */
public final class PostOrGet {

	private static final Logger _LOGGER = LoggerFactory.getLogger(PostOrGet.class);

	private final String encoding;

	public PostOrGet(String encoding) {
		this.encoding = encoding;
		_LOGGER.info(StringX.stringFormat("当前对象发送请求编码为：[0]", this.encoding));
	}


	//-----------------------------此段代码copy【jmt_interface】-----------------------------------------------

	public static String sendPost(String url, String param, String contentType) {
		if (contentType == null)
			contentType = "text/html; charset=utf-8";
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		URLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			_FakeX509TrustManager.allowAllSSL();
			conn = realUrl.openConnection();
			conn.setRequestProperty("content-type", contentType);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			if(param != null){
				out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
				out.write(param);
				out.flush();
			}
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

			String line = null;
			while ((line = in.readLine()) != null) {
				result.append(line + "\\n");
			}
			if (result != null) {
				result = result.delete(result.length() - 2, result.length());
			}
			line = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "数据传输错误!";
		} finally {
			conn = null;
			try {
				if (out != null) {
					out.close();
				}
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	public static class _FakeX509TrustManager implements X509TrustManager {
		private static TrustManager[] trustManagers;
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[0];

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public boolean isClientTrusted(X509Certificate[] chain) {
			return true;
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return true;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return _AcceptedIssuers;
		}

		public static void allowAllSSL() {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			SSLContext context = null;
			if (trustManagers == null) {
				trustManagers = new TrustManager[] { new _FakeX509TrustManager() };
			}
			try {
				context = SSLContext.getInstance("TLS");
				context.init(null, trustManagers, new SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}

			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		}
	}

	//-----------------------------------------------------------------------------------

	/**
	 * 预编译get请求的请求url
	 * 
	 * @param namedValues
	 * @return 完整的预编译url，包含参数信息
	 */
	public final String preGetRequestUrl(String requestUrl, ESendHTTPModel sendHTTPModel, NamedValue... namedValues) {
		_LOGGER.info("开始预编译get请求的请求url！");
		StringBuffer urlBuffer = new StringBuffer();
		_LOGGER.info("准备处理请求参数信息！");
		String params = this.handleParameters(sendHTTPModel, namedValues);
		_LOGGER.info("开始拼接完整url请求信息！");
		urlBuffer.append(requestUrl).append(ContextContast._QUESTION_MARK).append(params);
		_LOGGER.info(StringX.stringFormat("预编译get请求的请求url完整信息为：[0]", urlBuffer.toString()));
		return urlBuffer.toString();
	}

	/**
	 * 发送get请求！
	 * 
	 * @param requestUrl
	 * @param sendHTTPModel
	 * @param namedValues
	 * @return
	 */
	public final String sendGet(String requestUrl, ESendHTTPModel sendHTTPModel, NamedValue... namedValues) {
		_LOGGER.info("准备开始执行发送get请求任务！");
		StringBuffer resultBuffer = new StringBuffer();
		this.getRequestProcess(this.preGetRequestUrl(requestUrl, sendHTTPModel, namedValues), resultBuffer);
		_LOGGER.info(StringX.stringFormat("远程服务接口返回结果为：[0]", resultBuffer.toString()));
		return resultBuffer.toString();
	}

	/**
	 * 发送post请求
	 * 
	 * @param requestUrl
	 * @param sendHTTPModel
	 * @param namedValues
	 * @return
	 */
	public final String sendPost(String requestUrl, ESendHTTPModel sendHTTPModel, NamedValue... namedValues) {
		StringBuffer resultBuffer = new StringBuffer();
		_LOGGER.info("准备处理请求参数信息！");
		String params = this.handleParameters(sendHTTPModel, namedValues);
		this.postRequestProcess(requestUrl, resultBuffer, params);
		return resultBuffer.toString();
	}
	/**
	 * 发送post请求
	 *
	 * @param requestUrl
	 * @param sendHTTPModel
	 * @param list
	 * @return
	 */
	public final String sendPost(String requestUrl, ESendHTTPModel sendHTTPModel, List<NamedValue> list) {
		StringBuffer resultBuffer = new StringBuffer();
		_LOGGER.info("准备处理请求参数信息！");
		String params = this.handleParameters(sendHTTPModel, list);
		this.postRequestProcess(requestUrl, resultBuffer, params);
		return resultBuffer.toString();
	}
	/**
	 * 处理参数
	 * 
	 * @param sendHTTPModel
	 * @param namedValues
	 * @return
	 */
	private String handleParameters(ESendHTTPModel sendHTTPModel, NamedValue[] namedValues) {
		_LOGGER.info("开始进行处理参数信息动作！");
		StringBuffer params = new StringBuffer();
		if (sendHTTPModel == ESendHTTPModel._SEND_MODEL_ENCODE) {
			_LOGGER.info("准备进行转码动作！");
			this.encodeParameters(namedValues);
		}
		_LOGGER.info("准备进行拼接操作动作！");
		params = this.joinParameters(namedValues);
		_LOGGER.info(StringX.stringFormat("处理参数信息动作执行完毕，执行结果为：[0]", params.toString()));
		return params.toString();
	}
	private String handleParameters(ESendHTTPModel sendHTTPModel, List<NamedValue> list) {
		_LOGGER.info("开始进行处理参数信息动作！");
		StringBuffer params = new StringBuffer();
		if (sendHTTPModel == ESendHTTPModel._SEND_MODEL_ENCODE) {
			_LOGGER.info("准备进行转码动作！");
			this.encodeParameters(list.toArray(new NamedValue[]{}));
		}
		_LOGGER.info("准备进行拼接操作动作！");
		params = this.joinParameters(list.toArray(new NamedValue[]{}));
		_LOGGER.info(StringX.stringFormat("处理参数信息动作执行完毕，执行结果为：[0]", params.toString()));
		return params.toString();
	}
	/**
	 * 转码参数信息
	 * 
	 * @param namedValues
	 */
	private void encodeParameters(NamedValue[] namedValues) {
		_LOGGER.info("开始进行转码动作！");
		if (null == namedValues || namedValues.length == 0) {
			_LOGGER.info("参数列表为空，无法进行转码操作！");
			return;
		}
		for (int i = 0; i < namedValues.length; i++) {
			NamedValue namedValue = namedValues[i];
			_LOGGER.info(StringX.stringFormat("开始针对第[0]个参数进行转码操作，操作信息为：{[1],[2]}", i, namedValue.getName(),
					namedValue.getValue()));
			if (StringX.isEmpty(namedValue.getName())) {
				continue;
			}
			try {
				if (StringX.isNull(namedValue.getValue())) {
					_LOGGER.info(StringX.stringFormat("[0]的值为null，自动转换为空字符串！", namedValue.getName()));
					namedValues[i] = new NamedValue(namedValue.getName(),
							URLEncoder.encode(ContextContast._EMPTY_STRING, this.encoding));
				} else {
					namedValues[i] = new NamedValue(namedValue.getName(),
							URLEncoder.encode(namedValue.getValue(), this.encoding));
				}
			} catch (UnsupportedEncodingException e) {
				_LOGGER.error("url转码发生异常！");
				_LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 拼接参数
	 * 
	 * @param namedValues
	 * @return
	 */
	private StringBuffer joinParameters(NamedValue[] namedValues) {
		_LOGGER.info("开始进行拼接参数动作！");
		StringBuffer params = new StringBuffer();
		if (null == namedValues) {
			_LOGGER.info("参数数组为空，不进行拼接操作！");
			return params;
		} else {
			for (int i = 0; i < namedValues.length; i++) {
				NamedValue namedValue = namedValues[i];
				_LOGGER.info(StringX.stringFormat("当前拼接第[0]个参数,参数信息为：{[1]:[2]}", i, namedValue.getName(),
						namedValue.getValue()));
				if (StringX.isEmpty(namedValue.getName()))
					continue;
				params.append(namedValue.getName()).append("=");
				if (StringX.isEmpty(namedValue.getValue()))
					params.append(ContextContast._EMPTY_STRING);
				else
					params.append(namedValue.getValue());
				if (i != namedValues.length - 1)
					params.append("&");
			}
			_LOGGER.info(StringX.stringFormat("参数拼接结束！拼接结果为：[0]", params.toString()));
		}
		return params;
	}

	/*
	 * post具体请求过程
	 */
	private final void postRequestProcess(String url, StringBuffer result, String stringParams) {
		PrintWriter write = null;
		BufferedReader reader = null;
		try {
			URL netUrl = new URL(url);
			URLConnection conn = netUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			write = new PrintWriter(conn.getOutputStream());
			write.println(stringParams);
			write.flush();
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),this.encoding));
			String line = null;
			while (null != (line = reader.readLine())) {
				result.append(line);
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (null != write) {
					write.close();
				}
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * get请求具体过程
	 */
	private final void getRequestProcess(String url, StringBuffer result) {
		BufferedReader reader = null;
		try {
			URL netUrl = new URL(url);
			URLConnection conn = netUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.connect();
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),this.encoding));
			String line = null;
			while (null != (line = reader.readLine())) {
				result.append(line);
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}


}
