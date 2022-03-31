package org.zrclass.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换工具类
 * @author zhourui
 * @date 2020/3/3 9:19
 */
public class DateUtil {

    /**
     * 字符串转日期
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date formatDate(String dateString) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    /**
     * 日期转字符串
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf1.format(date);
        return dateString;
    }

    /**
     * 日期转字符串yyyyMMddHHmmss
     * @param date
     * @return
     */
    public static String formatDateString(Date date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf1.format(date);
        return dateString;
    }

    /**
     * 字符串转日期
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date formatDateCsv(String dateString) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date;
        try {
            date = sdf1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

}
