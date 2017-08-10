package org.jumutang.giftpay.entity;

import java.io.Serializable;

public class CodeMess implements Serializable {

	private String code;
	private String mess;

	public CodeMess(){

	}
	public CodeMess(String code, String mess) {
		super();
		this.code = code;
		this.mess = mess;
	}

	public String getCode() {
		return code;
	}

	public String getMess() {
		return mess;
	}

}
