package org.jumutang.giftpay.tools;

/**
 * Http发送模式
 * @author YuanYu
 *
 */
public enum ESendHTTPModel {

	_SEND_MODEL_ENCODE("ENCODE","URL转码发送模式","ENCODE"),
	_SEND_MODEL_DECODER("DECODER","明文发送模式","DECODER");

	private final String java_code;
	private final String view_show;
	private final String data_save;

	private ESendHTTPModel(String java_code, String view_show, String data_save) {
		this.java_code = java_code;
		this.view_show = view_show;
		this.data_save = data_save;
	}

	public String getJava_code() {
		return java_code;
	}

	public String getView_show() {
		return view_show;
	}

	public String getData_save() {
		return data_save;
	}
	
	
}
