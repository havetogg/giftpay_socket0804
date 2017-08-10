package org.jumutang.giftpay.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @comment String类的拓展器
 * @author YuanYu
 * @date 2015年7月23日-下午4:42:57
 */
public final class StringX {

	private StringX() {
	}

	/**
	 * @comment 格式化字符串 demo
	 *          StringExtender.stringFormat("测试[0][1]回家的[2],[1]很开心","案例：","小明",
	 *          "路上");
	 * @author YuanYu
	 * @date 2015年7月23日-下午4:44:01
	 */
	public static final String stringFormat(String content, Object... params) {
		if (null == content) {
			throw new NullPointerException("格式化内容不可为空");
		}
		String regEx = "\\[[\\d]*\\]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(content);
		while (true) {
			if (!matcher.find()) {
				break;
			}
			content = content.replace(matcher.group(0), String.valueOf(params[generateIndex(matcher.group(0))]));
		}
		return content;
	}

	/**
	 * @comment 取索引 [0] ----> 0
	 * @author YuanYu
	 * @date 2015年7月23日-下午4:45:09
	 */
	private static final int generateIndex(String group) {
		String regExIndex = "[\\d*]";
		Pattern pattern = Pattern.compile(regExIndex);
		Matcher matcher = pattern.matcher(group);
		if (!matcher.find()) {
			return 0;
		}
		return Integer.valueOf(matcher.group(0));
	}

	/**
	 * 字符串为 null 或者 “”
	 * 
	 * @param content
	 *            校验类容
	 * @return 布尔变量
	 */
	public static final boolean isEmpty(String content) {
		if (null == content || "".equals(content.trim())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符串不为null 或者 “”
	 * 
	 * @param content
	 *            校验类容
	 * @return 布尔变量
	 */
	public static final boolean isNotEmpty(String content) {
		if (null != content && !"".equals(content.trim())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 校验字符串为null
	 * 
	 * @param content
	 * @return 布尔值
	 */
	public static final boolean isNull(String content) {
		if (null == content) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 校验字符串不为null
	 * 
	 * @param content
	 * @return 布尔值
	 */
	public static final boolean isNotNull(String content) {
		if (null != content) {
			return true;
		} else {
			return false;
		}
	}

	public static final String nullToEmpty(String content){
		return null == content ? "":content.trim();
	}
	
}
