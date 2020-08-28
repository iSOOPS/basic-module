package com.isoops.basicmodule.source;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Date;

public class STime {

    private static final long YAR = 365 * 24 * 60 * 60;
    private static final long DAY  = 24 * 60 * 60;
    private static final long HOU = 60 * 60;
    private static final long MIN  = 60;

    /**
     * date 转 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime 转 date
     */
    public static Date toDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 时间戳 转 LocalDateTime
     * @param timeMill 时间戳 毫秒
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Long timeMill) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMill), ZoneId.systemDefault());
    }
    /**
     * LocalDateTime 转 时间戳
     * @param time LocalDateTime
     * @return 时间戳 毫秒
     */
    public static Long toTimeMill(LocalDateTime time) {
        Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 获取当前时间文本
     * @return 文本
     */
    public static String formatNow() {
        return  formatTime(LocalDateTime.now(), null);
    }

    /**
     * 转化指定时间/指定格式为文本
     * @param time 时间
     * @param pattern 格式
     * @return 文本
     */
    public static String formatTime(LocalDateTime time,String pattern) {
        if (pattern == null || pattern.length() < 1){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    private static String getFormatName(long defaultTime){
        if (defaultTime == YAR){
            return "年";
        }
        else if (defaultTime == DAY){
            return "天";
        }
        else if (defaultTime == HOU){
            return "小时";
        }
        else if (defaultTime == MIN){
            return "分";
        }
        else{
            return "秒";
        }
    }

    private static String formatString(Long lastMill, long defaultTime){
        long t = (lastMill - (lastMill % defaultTime != lastMill ? lastMill % defaultTime : 0 ))  / defaultTime;
        return t !=0 ? t + getFormatName(defaultTime) : "";
    }

    public static String formatTimeMill(Long timeMill) {
        long lastMill = timeMill;

        String year = formatString(lastMill,YAR);
        lastMill = lastMill % YAR;
        String day = formatString(lastMill,DAY);
        lastMill = lastMill % DAY;
        String hou = formatString(lastMill,HOU);
        lastMill = lastMill % HOU;
        String min = formatString(lastMill,MIN);
        lastMill = lastMill % MIN;
        String sen = lastMill !=0 ? lastMill + getFormatName(lastMill) : "";

        return year + day + hou + min + sen;
    }
    /**
     * 修改时间的值
     * @param time 时间
     * @param add 增加或减少
     * @param offset 增减偏移数量
     * @param field 操作单位年月日时分秒毫秒
     * @return 时间
     */
    public static LocalDateTime edit(LocalDateTime time,Boolean add, Integer offset, TemporalUnit field) {
        return add ? time.plus(offset, field) : time.minus(offset,field);
    }

    /**
     * 获取两个日期的差  field参数为ChronoUnit.*
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param field  单位(年月日时分秒)
     * @return 年月日时分秒长度
     */
    public static Long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        if (field == ChronoUnit.YEARS) return (long) period.getYears();
        if (field == ChronoUnit.MONTHS) return (long) (period.getYears() * 12 + period.getMonths());
        return field.between(startTime, endTime);
    }

    /**
     * 获取某个时间/某个时间当月/某个时间当年的0点时间
     * @param time 时间
     * @param field 年月日
     * @return 0点时间
     */
    public static LocalDateTime startTime(LocalDateTime time,ChronoUnit field) {
        switch (field){
            case DAYS:
                return time.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case MONTHS:
                return time.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case YEARS:
                return time.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            default:
                return null;
        }
    }

    /**
     * 获取某个时间/某个时间当月/某个时间当年的23:59:59点时间
     * @param time 时间
     * @param field 年月日
     * @return 23:59:59点时间
     */
    public static LocalDateTime endTime(LocalDateTime time,ChronoUnit field) {
        switch (field){
            case DAYS:
                return time.withHour(23).withMinute(59).withSecond(59).withNano(99);
            case MONTHS:
                return time.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(99);
            case YEARS:
                return time.with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).withNano(99);
            default:
                return null;
        }
    }


}
