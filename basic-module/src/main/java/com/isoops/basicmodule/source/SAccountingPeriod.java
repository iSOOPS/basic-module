package com.isoops.basicmodule.source;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

public class SAccountingPeriod {

    private static Integer empty2018 = 2;

    /**
     * 通过日期获取当前会计月份
     * @param date 日期>2018.1.1
     * @return f
     */
    public static Integer getApMonth(LocalDate date){
        //必须是2017年之后的计算
        if (date.getYear()<2018){
            return 0;
        }

        Integer emptyStartDay = SAccountingPeriod.empty2018;//默认2018年头空2天
        for (int i = 2019; i <= date.getYear(); i++) {//若输入为2018年，则不走for循环
            Integer emptyLastDay = 371 - 365 - emptyStartDay - ((i-1)%4==0 ? 1 : 0);//计算2018年末空了几天
            emptyStartDay = 7 - emptyLastDay;//7-2018年末空了几天=2019年头空了几天
        }

        Integer dayTag= date.get(ChronoField.DAY_OF_YEAR);//获取输入日期为那年的第几天
        return SAccountingPeriod.getApMonthByInteger(dayTag+emptyStartDay);//输入天数 为那年的第几天+年头空的数量=总数量
    }

    private static List<Integer> getDayList(){
        Integer year = LocalDate.now().getYear();
        return Arrays.asList(31,year%4==0 ? 29 : 28,31,30,31,30,31,31,30,31,30,31);
    }

    /**
     * 会计月获取
     * @param dayCount 今年当第几天(1月1日前当空余也包括)
     * @return f
     */
    private static Integer getApMonthByInteger(Integer dayCount){
        //按照1年 445*4个季度的计算，通过当前 总天数+年头空的天数，确定为那个月份
        Integer quarterCount = 28 * 3 + 7;

        if (dayCount > 0                         && dayCount <= 28){
            return 1;
        }
        if (dayCount > 28                        && dayCount <= 28*2){
            return 2;
        }
        if (dayCount > 28 * 2                    && dayCount <= quarterCount){
            return 3;
        }
        if (dayCount > quarterCount              && dayCount <= quarterCount + 28){
            return 4;
        }
        if (dayCount > quarterCount + 28         && dayCount <= quarterCount + 28 * 2){
            return 5;
        }
        if (dayCount > quarterCount + 28 * 2     && dayCount <= quarterCount * 2){
            return 6;
        }
        if (dayCount > quarterCount * 2          && dayCount <= quarterCount * 2 + 28){
            return 7;
        }
        if (dayCount > quarterCount * 2 + 28     && dayCount <= quarterCount * 2 + 28){
            return 8;
        }
        if (dayCount > quarterCount * 2 + 28     && dayCount <= quarterCount * 2 + 28 * 2){
            return 9;
        }
        if (dayCount > quarterCount * 2 + 28 * 2 && dayCount <= quarterCount * 3){
            return 10;
        }
        if (dayCount > quarterCount * 3          && dayCount <= quarterCount * 3 + 28){
            return 11;
        }
        if (dayCount > quarterCount * 3 + 28     && dayCount <= quarterCount * 4){
            return 12;
        }
        return 0;
    }





}
