package org.jumutang.giftpay.tools;

import org.jumutang.giftpay.entity.CodeMess;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodePools {

	private static final Map<String, CodeMess> _CODE_MESS_POOLS = new HashMap<String, CodeMess>();
	/** 操作成功! */
	public static final String _SUCCESS_1 = "1";
	/** 操作失败! */
	public static final String _FAIL_0 = "0";
	/** 用户注册失败! */
	public static final String _FAIL_10000 = "10000";
	/** 用户未注册! */
	public static final String _FAIL_10001 = "10001";
	/** 验证码不正确 */
	public static final String _FAIL_10002 = "10002";
	/** 未发送验证码 */
	public static final String _FAIL_10003 = "10003";
	/** 用户授权信息失效 */
	public static final String _FAIL_10004 = "10004";
	/** 验证码已失效 */
	public static final String _FAIL_10005 = "10005";

	static {
		_CODE_MESS_POOLS.put(_SUCCESS_1, new CodeMess(_SUCCESS_1, "操作成功!"));
		_CODE_MESS_POOLS.put(_FAIL_0, new CodeMess(_FAIL_0, "操作失败!"));
		_CODE_MESS_POOLS.put(_FAIL_10000, new CodeMess(_FAIL_10000, "用户注册失败!"));
		_CODE_MESS_POOLS.put(_FAIL_10001, new CodeMess(_FAIL_10001, "用户未注册!"));
		_CODE_MESS_POOLS.put(_FAIL_10002, new CodeMess(_FAIL_10002, "验证码不正确!"));
		_CODE_MESS_POOLS.put(_FAIL_10003, new CodeMess(_FAIL_10003, "未发送验证码!"));
		_CODE_MESS_POOLS.put(_FAIL_10004, new CodeMess(_FAIL_10004, "用户授权信息失效!"));
		_CODE_MESS_POOLS.put(_FAIL_10005, new CodeMess(_FAIL_10005, "验证码已失效"));
	}

	public static final CodeMess getCodeMess(String code) {
		return _CODE_MESS_POOLS.get(code);
	}

	public static final String getMess(String code) {
		return _CODE_MESS_POOLS.get(code).getMess();
	}

}
