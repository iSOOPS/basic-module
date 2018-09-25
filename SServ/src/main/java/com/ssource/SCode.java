package com.ssource;

import com.redissouce.SRedis;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SCode {

    /**
     * 生成唯一号/长
     * @param businessCode 应用程序号/2位
     * @param deskCode  前缀/2位
     * @param machineCode 机器码/2位
     * @param autoKey 自增id的key
     * @return 应用程序号2位 + 前缀2位 + 时间戳10位 + 机器码2位 + redis自增8位 共计24位
     */
    public static String createNormalCode(Integer businessCode,
                                          Integer deskCode,
                                          Integer machineCode,
                                          String autoKey) {
        if (autoKey == null || autoKey.equals("")){
            return null;
        }
        if (businessCode < 0 || businessCode > 99){
            return null;
        }
        if (deskCode < 0 || deskCode > 99){
            return null;
        }
        if (machineCode < 0 || machineCode > 99){
            return null;
        }
        Long autoCode = SRedis.incr(autoKey);
        if (autoCode == null || autoCode > 99999999) {
            return null;
        }
        String autoString = String.format("%08d", autoCode);
        return String.format("%02d", businessCode) + String.format("%02d", deskCode) + SClass.timeMillis() + String.format("%02d", machineCode) + autoString;
    }

    /**
     * 生成唯一号/短
     * @param businessCode 应用程序号/2位
     * @param deskCode  前缀/2位
     * @param autoKey 自增id的key
     * @return 应用程序号2位 + 前缀2位 + 日期年月日6位 + redis自增6位 共计16位
     */
    public static String createSortCode(Integer businessCode,
                                        Integer deskCode,
                                        String autoKey) {
        if (autoKey == null || autoKey.equals("")){
            return null;
        }
        if (businessCode < 0 || businessCode > 99){
            return null;
        }
        if (deskCode < 0 || deskCode > 99){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        Date date = new Date(SClass.timeMillisLong() * 1000);
        String dateString = simpleDateFormat.format(date);
        Long autoCode = SRedis.incr(autoKey);
        if (autoCode == null || autoCode > 999999) {
            return null;
        }
        String autoString = String.format("%06d", autoCode);
        return String.format("%02d", businessCode) + String.format("%02d", deskCode) + dateString + autoString;
    }

    /**
     * 创建附加号
     * @param basicCode 基础号
     * @param subCode 附加号
     * @param autoKey 自增id的key
     * @return f
     */
    public static String createAdderCode(String basicCode,
                                       String subCode,
                                       String autoKey) {
        if (basicCode == null || basicCode.equals("")){
            return null;
        }
        if (autoKey == null || autoKey.equals("")){
            return null;
        }
        Long autoCode = SRedis.incr(autoKey);
        if (autoCode == null || autoCode > 999999) {
            return null;
        }
        String autoString = String.format("%06d", autoCode);
        return basicCode + (subCode == null ? "" : subCode) + autoString;
    }
}
