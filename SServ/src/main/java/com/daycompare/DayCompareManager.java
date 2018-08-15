package com.daycompare;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by samuel on 24/02/2018.
 */
public class DayCompareManager {
    /**
     * 计算2个日期之间相差的  相差多少年月日
     * 比如：2011-02-02 到  2017-03-02 相差 6年，1个月，0天
     * @param fromTimetemp
     * @param toTimetemp
     * @return
     */
    public static DayCompare dayComparePrecise(String fromTimetemp,String toTimetemp){
        Date fromDate = timeToDate(fromTimetemp);
        Date toDate = timeToDate(toTimetemp);
        Calendar from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_YEAR);
        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_YEAR);
        int year = toYear  -  fromYear;
        int month = toMonth - fromMonth + year * 12;
        int day = toDay - fromDay + year * 365;
        DayCompare dayCompare = new DayCompare();
        dayCompare.setDay(day);
        dayCompare.setMonth(month);
        dayCompare.setYear(year);
        return dayCompare;
    }

    public static Date timeToDate(String time){
        long lt = new Long(time);
        Date date = new Date(lt*1000);
        return date;
    }
    public static String timeToDateForm(String seconds) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
    /**
     *
     * @param minDate 最小时间  2015-01
     * @param maxDate 最大时间 2015-10
     * @return 日期集合 格式为 年-月
     * @throws Exception
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        String fromDate = timeToDateForm(minDate);
        String toDate = timeToDateForm(maxDate);

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(fromDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(toDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(String.valueOf(curr.getTime().getTime()/1000));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }


}
