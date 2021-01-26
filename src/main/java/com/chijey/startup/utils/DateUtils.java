package com.chijey.startup.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期处理工具类
 *
 * @author yangfeng
 * @date 2018年10月22日 下午15:36:25
 * Email: Feng.Yang@things-matrix.com
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String ATT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss+00:00";
    public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String UTC_PATTERN_HOUR = "yyyy-MM-dd'T'00:00:00'Z'";
    public static final String UTC_PATTERN_HOUR_HH = "yyyy-MM-dd'T'HH:00:00'Z'";
    public static final String TIME_PATTERN = "HH:mm";
    public static final String HOUR_PATTERN = "yyyy-MM-dd HH:00:00";
    public static final String MINIT_PATTERN = "yyyy-MM-dd HH:mm:00";
    public static final String UTC_MILLI_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String COMMAND_PATTERN = "yyyyMMddHHmmssSSS";

    public enum TimeSet {
        YEAR, MONTH, WEEK, DAY, HOUR, MIN, SECOND, MILLISECOND
    }
    /**
     * 解析日期
     */
    public static Date parseDate(String dateStr) {
        SimpleDateFormat format = null;
        if (StringUtils.isBlank(dateStr)) return null;

        String _dateStr = dateStr.trim();
        try {
            if (_dateStr.matches("\\d{1,2}[A-Z]{3}")) {
                _dateStr = _dateStr + (Calendar.getInstance().get(Calendar.YEAR) - 2000);
            }

            if (_dateStr.matches("\\d{1,2}[A-Z]{3}\\d{2}")) {// 01OCT12
                format = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            } else if (_dateStr.matches("\\d{1,2}[A-Z]{3}\\d{4}.*")) {// 01OCT2012 1224,01OCT2012 12:24
                _dateStr = _dateStr.replaceAll("[^0-9A-Z]", "").concat("000000").substring(0, 15);
                format = new SimpleDateFormat("ddMMMyyyyHHmmss", Locale.ENGLISH);
            } else {
                try {
                    new SimpleDateFormat(TIMESTAMP_PATTERN).parse(_dateStr);
                    format = new SimpleDateFormat(TIMESTAMP_PATTERN);
                } catch (ParseException e) {
                    StringBuffer sb = new StringBuffer(_dateStr);
                    String[] tempArr = _dateStr.split("\\s+");
                    tempArr = tempArr[0].split("-|\\/");
                    if (tempArr.length == 3) {
                        if (tempArr[1].length() == 1) {
                            sb.insert(5, "0");
                        }
                        if (tempArr[2].length() == 1) {
                            sb.insert(8, "0");
                        }
                    }
                    _dateStr = sb.append("000000").toString().replaceAll("[^0-9]", "").substring(0, 14);
                    if (_dateStr.matches("\\d{14}")) {
                        format = new SimpleDateFormat("yyyyMMddHHmmss");
                    }
                }
            }

            Date date = format.parse(_dateStr);
            return date;
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date characters[" + dateStr + "]");
        }
    }

    /**
     * 解析日期字符串转化成日期格式
     */
    public static Date parseDate(String dateStr, String pattern) {
        try {
            if (StringUtils.isBlank(dateStr)) {
                return null;
            }

            if (StringUtils.isNotBlank(pattern)) {
                return new SimpleDateFormat(pattern).parse(dateStr);
            }

            return parseDate(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date characters[" + dateStr + "]");
        }
    }

    /**
     * 解析日期字符串转化UTC成日期格式
     */
    public static Date parseUTCDate(String dateStr, String pattern) {
        try {
            if (StringUtils.isBlank(dateStr)) {
                return null;
            }

            if (StringUtils.isNotBlank(pattern)) {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                return format.parse(dateStr);
            }

            return parseDate(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date characters[" + dateStr + "]");
        }
    }

    /**
     * 获取指定时间的时间戳
     * @param dateStr
     * @return
     */
    public static long getTimeStamp(String dateStr) {
        try {
            return parseDate(dateStr).getTime();
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date characters[" + dateStr + "]");
        }
    }

    /**
     * 获得指定时间一天、一周、一月、一年的起始时间
     * @param date
     * @param TimeSet
     * @return
     */
    public static Date getBeginTime(Date date, TimeSet TimeSet) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        switch (TimeSet) {
            case DAY:
                now.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case WEEK:
                now.set(Calendar.DAY_OF_WEEK, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case MONTH:
                now.set(Calendar.DAY_OF_MONTH, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case YEAR:
                now.set(Calendar.MONTH, 0);
                now.set(Calendar.DAY_OF_YEAR, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                break;
            default:
                break;
        }

        return now.getTime();
    }

    /**
     * 获得指定时间一天、一周、一月、一年的起始时间
     * @param date
     * @param TimeSet
     * @return
     */
    public static Date getEndTime(Date date, TimeSet TimeSet) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        switch (TimeSet) {
            case DAY:
                now.set(Calendar.HOUR_OF_DAY, 0);
                now.add(Calendar.DAY_OF_YEAR, 1);
                now.add(Calendar.MILLISECOND, -1);
                break;
            case WEEK:
                now.set(Calendar.DAY_OF_WEEK, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                now.add(Calendar.WEEK_OF_YEAR, 1);
                now.add(Calendar.MILLISECOND, -1);
                break;
            case MONTH:
                now.set(Calendar.DAY_OF_MONTH, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                now.add(Calendar.MONTH, 1);
                now.add(Calendar.MILLISECOND, -1);
                break;
            case YEAR:
                now.set(Calendar.MONTH, 0);
                now.set(Calendar.DAY_OF_YEAR, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                now.add(Calendar.YEAR, 1);
                now.add(Calendar.MILLISECOND, -1);
                break;
            default:
                break;
        }

        return now.getTime();
    }

    /**
     * 格式化日期字符串
     */
    public static String formatDateStr(String dateStr, String... patterns) {
        String pattern = TIMESTAMP_PATTERN;
        if (patterns != null && patterns.length > 0 && StringUtils.isNotBlank(patterns[0])) {
            pattern = patterns[0];
        }

        return DateFormatUtils.format(parseDate(dateStr), pattern);
    }

    /**
     * 格式化时间戳
     * @param timeStamp
     * @param patterns
     * @return
     */
    public static String formatTimeStamp(Long timeStamp, String... patterns) {
        try {
            String pattern = TIMESTAMP_PATTERN;
            if (patterns != null && patterns.length > 0 && StringUtils.isNotBlank(patterns[0])) {
                pattern = patterns[0];
            }

            return DateFormatUtils.format(timeStamp, pattern);
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date timestamp[" + timeStamp + "]");
        }
    }

    /**
     * 格式化日期为日期字符串
     */
    public static String  format(Date date, String... patterns) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        String pattern = TIMESTAMP_PATTERN;
        if (patterns != null && patterns.length > 0 && StringUtils.isNotBlank(patterns[0])) {
            pattern = patterns[0];
        }

        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 格式化日期为指定格式
     */
    public static Date formatDate(Date date, String... patterns) {
        String pattern = TIMESTAMP_PATTERN;
        if (patterns != null && patterns.length > 0 && StringUtils.isNotBlank(patterns[0])) {
            pattern = patterns[0];
        }

        return parseDate(DateFormatUtils.format(date, pattern));
    }

    /**
     * 比较两个时间相差多少秒
     */
    public static long getDiffSeconds(Date d1, Date d2) {
        return Math.abs((d2.getTime() - d1.getTime()) / 1000);
    }

    /**
     * 比较两个时间相差多少分钟
     */
    public static long getDiffMinutes(Date d1, Date d2) {
        long diffSeconds = getDiffSeconds(d1, d2);
        return diffSeconds / 60;
    }

    /**
     * 比较两个时间相差多少天
     */
    public static long getDiffDay(Date d1, Date d2) {
        long between = Math.abs((d2.getTime() - d1.getTime()) / 1000);
        long day = between / 60 / 60 / 24;
        return (long) Math.floor(day);
    }

    /**
     * 返回传入时间月份的第一天
     */
    public static Date firstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int value = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        return cal.getTime();
    }

    /**
     * 返回传入时间月份的最后一天
     */
    public static Date lastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        return cal.getTime();
    }

    /**
     * 获取两个时间相差月份
     */
    public static int getDiffMonth(Date start, Date end) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);

        return (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12
                + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    /**
     * 获取两个时间的之间的每一天
     */
    public static List<String> getTwoDaysDay(String dateStart, String dateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = new ArrayList<>();
        try {
            Date dateOne = sdf.parse(dateStart);
            Date dateTwo = sdf.parse(dateEnd);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateOne);

            dateList.add(dateStart.split(" ")[0]);
            while (calendar.getTime().before(dateTwo)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dateList.add(sdf.format(calendar.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateList;
    }

    /**
     * 计算并格式化消耗时间
     */
    public static String formatTimeConsumingInfo(long startPoint) {
        StringBuffer buff = new StringBuffer();
        long totalMilTimes = System.currentTimeMillis() - startPoint;
        int hour = (int) Math.floor(totalMilTimes / (60 * 60 * 1000));
        int mi = (int) Math.floor(totalMilTimes / (60 * 1000));
        int se = (int) Math.floor((totalMilTimes - 60000 * mi) / 1000);
        if (hour > 0) {
            buff.append(hour).append("小时");
        }
        if (mi > 0) {
            buff.append(mi).append("分");
        }
        if (hour == 0) {
            buff.append(se).append("秒");
        }
        return buff.toString();
    }

    /**
     * 判断是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 判断一个时间是否在某段时间区间内--HH:mm-HH:mm
     */
    public static Boolean isBelong(Date date, String beginTime, String endTime) {
        SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN);//设置日期格式
        Date now = null;
        Date startDate = null;
        Date endDate = null;
        try {
            now = df.parse(df.format(new Date()));
            startDate = df.parse(beginTime);
            endDate = df.parse(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(now);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        return nowDate.after(begin) && nowDate.before(end);
    }

    /**
     * 获取当前时间的前一天或者前几天时间 -1为前一天，1为后一天
     */
    public static Date getDate(int num) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        date = calendar.getTime();
        return formatDate(date, DATE_PATTERN);
    }

    /**
     * 获取指定时间的前一天或者前几天时间 -1为前一天，1为后一天
     */
    public static Date getDate(String dateStr, int num) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            Date date = new SimpleDateFormat(TIMESTAMP_PATTERN).parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, num);
            date = calendar.getTime();
            return formatDate(date, DATE_PATTERN);
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date characters[" + dateStr + "]");
        }
    }

    public static Long parseTimeStamp(Object dateObj) {
        if (null == dateObj) {
            return null;
        }

        String dateStr = "";
        try {
            dateStr = dateObj instanceof Date ? String.valueOf(((Date) dateObj).getTime()) : dateObj.toString().trim();

            if (dateStr.matches("[+-]*\\d+\\.?\\d*[Ee]*[+-]*\\d+")) {
                dateStr = NumberUtils.createBigDecimal(dateStr).toPlainString();
            } else if (dateStr.matches("^\\d{4,9}-\\d{1,2}-\\d{1,2}\\s*T{0,1}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}(?:.\\d{0,9})Z{0,1}")) {
                Date date = DateUtils.parseDate(formatDateStr(dateStr, UTC_PATTERN), UTC_PATTERN);
                dateStr = String.valueOf(date.getTime());
            }

            if (StringUtils.isNotBlank(dateStr) && NumberUtils.isDigits(dateStr)) {
                dateStr = StringUtils.rightPad(StringUtils.substring(dateStr, 0, 13), 13, "0");
                return Long.parseLong(dateStr);
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("cannot parse date characters[" + dateStr + "]");
        }
    }


}
