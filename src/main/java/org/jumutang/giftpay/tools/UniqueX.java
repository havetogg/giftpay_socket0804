package org.jumutang.giftpay.tools;

import java.util.UUID;

/**
 * 主键生成策略
 * 
 * @author YuanYu
 *
 */
public final class UniqueX {

	/**
	 * 获取唯一键值信息
	 * 
	 * @return 唯一键值
	 */
	public static final String randomUnique() {
		return MD5X.getUpperCaseMD5For16(getUUID());
	}

	/**
	 * 获取唯一键值信息
	 * 
	 * @param prefix
	 *            键值前缀
	 * @return 唯一键值
	 */
	public static final String randomUnique(String prefix) {
		return prefix.toUpperCase() + "-" + randomUnique();
	}
	
	public static final String randomUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 获取UUID信息
	 * 
	 * @return UUID
	 */
	private static final String getUUID() {
		return UUID.randomUUID().toString().toUpperCase();
	}
	
	public static void main(String[] args) {
		System.out.println(UniqueX.randomUnique());
	}
}
