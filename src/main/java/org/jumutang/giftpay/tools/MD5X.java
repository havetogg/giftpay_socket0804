package org.jumutang.giftpay.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具�?
 * @author YuanYu
 *
 */
public final class MD5X {
	
	private MD5X(){
	}
	
	private static final String getMD5(String sourceStr){
		String result = "";
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 16位大写md5摘要
	 * @param sourceStr
	 * @return
	 */
	public static final String getUpperCaseMD5For16(String sourceStr){
		String result = getMD5(sourceStr);
		return result.substring(8, 24).toUpperCase();
	}
	/**
	 * 32位大写md5摘要
	 * @param sourceStr
	 * @return
	 */
	public static final String getUpperCaseMD5For32(String sourceStr){
		String result = getMD5(sourceStr);
		return result.toUpperCase();
	}
	/**
	 * 16位小写md5摘要
	 * @param sourceStr
	 * @return
	 */
	public static final String getLowerCaseMD5For16(String sourceStr){
		String result = getMD5(sourceStr);
		return result.substring(8, 24).toLowerCase();
	}
	/**
	 * 32位小写md5摘要
	 * @param sourceStr
	 * @return
	 */
	public static final String getLowerCaseMD5For32(String sourceStr){
		String result = getMD5(sourceStr);
		return result.toLowerCase();
	}
	
	public static void main(String[] args) {
	}
}
