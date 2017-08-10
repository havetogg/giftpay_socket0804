package org.jumutang.giftpay.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间操作类
 *
 * @author 鲁雨
 * @version v1.0
 *          <p>
 *          copyright Luyu(18994139782@163.com)
 * @since 20170122
 */
public class DateFormatUtil {

    public static String formateString() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static boolean compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            //如果大于2分钟 true 否则false
            return ((dt2.getTime() - dt1.getTime())) > 1000 * 5;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取多少天的日期集合
     *
     * @return
     */
    public static List<String> queryDaysList(int i) {
        // 时间表示格式可以改变，yyyyMMdd需要写例如20160523这种形式的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 将字符串的日期转为Date类型，ParsePosition(0)表示从第一个字符开始解析
        String str = sdf.format(new Date());
        Date date = sdf.parse(str, new ParsePosition(0));
        List<String> strList = new ArrayList<String>();
        for (int d = 0; d < i; d++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -d);
            Date date1 = calendar.getTime();
            String out = sdf.format(date1);
            strList.add(out);
        }
        return strList;
    }


    /**
     * 获取多少天的日期
     *
     * @return
     */
    public static String queryDaysObject(int i) {
        // 时间表示格式可以改变，yyyyMMdd需要写例如20160523这种形式的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 将字符串的日期转为Date类型，ParsePosition(0)表示从第一个字符开始解析
        String str = sdf.format(new Date());
        Date date = sdf.parse(str, new ParsePosition(0));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -i);
        Date date1 = calendar.getTime();
        String out = sdf.format(date1);
        return out;
    }

}
