package org.jumutang.giftpay.tools;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 
 * @author YuanYu
 *
 * @param <T>
 */
public class ContextResult<T> {
	/**
	 * code信息标识失败或成功
	 */
	private String code;
	/**
	 * mess标识失败或者成功的具体描述信息
	 */
	private String mess;
	/**
	 * 结果集
	 */
	private List<T> resultList;
	/**
	 * 结果
	 */
	private T resultObject;
	/**
	 * code信息标识失败或成功
	 */
	public String getCode() {
		return code;
	}
	/**
	 * code信息标识失败或成功
	 */
	public ContextResult<T> setCode(String code) {
		this.code = code;
		return this;
	}
	/**
	 * code信息标识失败或成功
	 */
	public ContextResult<T> setCodeWithMess(String code) {
		this.code = code;
		String mess = ErrorCodePools.getMess(code);
		if (StringX.isNotEmpty(mess)) {
			this.mess = mess;
		}
		return this;
	}
	/**
	 * mess标识失败或者成功的具体描述信息
	 */
	public String getMess() {
		return mess;
	}
	/**
	 * mess标识失败或者成功的具体描述信息
	 */
	public ContextResult<T> setMess(String mess) {
		this.mess = mess;
		return this;
	}
	/**
	 * 结果集
	 */
	public List<T> getResultList() {
		return resultList;
	}
	/**
	 * 结果集
	 */
	public ContextResult<T> setResultList(List<T> resultList) {
		this.resultList = resultList;
		return this;
	}
	/**
	 * 结果
	 */
	public T getResultObject() {
		return resultObject;
	}
	/**
	 * 结果
	 */
	public ContextResult<T> setResultObject(T resultObject) {
		this.resultObject = resultObject;
		return this;
	}
	/**
	 * 返回json字符串
	 * @return
	 */
	public String toJsonString() {
		return JSONObject.toJSONString(this);
	}
}
