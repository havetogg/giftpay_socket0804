package org.jumutang.giftpay.tools;

import java.io.Serializable;

/**
 * 键值对
 * 
 * @author YuanYu
 *
 */
public final class NamedValue implements Serializable {

	private static final long serialVersionUID = 2590949210941405548L;

	private final String name;
	private final String value;

	public NamedValue(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
