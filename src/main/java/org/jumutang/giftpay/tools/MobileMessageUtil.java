package org.jumutang.giftpay.tools;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by RuanYJ on 2017/2/13.
 */
public class MobileMessageUtil {


   private static Logger logger = LoggerFactory.getLogger(MobileMessageUtil.class);

    private static final String SENDMSGURl="http://api.juxinbox.com/jxapi/sendMsm_doSendMessage";
    private static final String SENDMSGAPPID="5025dd19ca55bfa6b7b42939a49cadb9";

    public static void main(String[] args) throws UnsupportedEncodingException {
        Random random = new Random();
        int valNum = random.nextInt(899999);
        valNum = valNum + 100000;//随机六位数
        String msgContent="【有礼付】您的验证码是"+valNum+"，如非本人操作请忽略该短信。";
        System.out.println(MobileMessageUtil.sendMessage("18512525931,18652902341,17625907878",msgContent));
    }


    public static JSONObject sendMessage(String phone, String sendMsg) throws UnsupportedEncodingException {
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", SENDMSGAPPID);
        packageParams.put("mobile", phone);
        packageParams.put("content", sendMsg);

        String createSign = MobileMessageUtil.createSign(packageParams);
        packageParams.put("sign", createSign);
        System.out.println("createSign:"+createSign);
        logger.info("createSign:"+createSign);

        String strpost= JSONObject.fromObject(packageParams).toString();

        System.out.println("strpost:"+strpost);
        logger.info("strpost:"+strpost);
        String sendPost = MobileMessageUtil.post(SENDMSGURl,strpost);
        JSONObject jsonObject = JSONObject.fromObject(sendPost);

        System.out.println("result:"+sendPost);
        logger.info("result::"+sendPost);
        return jsonObject;
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     * @throws UnsupportedEncodingException
     */
    public static String createSign(SortedMap<String, String> packageParams) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        String str = sb.toString();
        String strec=str.substring(0, str.length()-1);
        System.out.println(strec);
        logger.info("strec:"+strec);
//		String sign = MD5Util.MD5Encode(UrlEncode(strec), "UTF-8");
        String sign = MobileMessageUtil.md5Encodenew(URLEncoder.encode(strec,"utf-8"));
        logger.info("sign:"+sign);
        return sign;
    }
    public static String md5Encodenew(String sourceStr){
        System.out.println(sourceStr);
        logger.info("sourceStr:"+sourceStr);
        System.out.println(sourceStr);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = null;
        try {
            byteArray = sourceStr.getBytes("UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    public static String post(String strURL, String params) {
        System.out.println(strURL);
        System.out.println(params);
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            out.flush();
            out.close();
            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            InputStream is = connection.getInputStream();
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                System.out.println(result);
                return result;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }

}
