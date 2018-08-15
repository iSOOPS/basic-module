package com.elasticsearch;


/**
 * Created by Samuel on 2016/11/9.
 */
public enum SESEnum {
    /**
     * -注释-
     * must 必须满足
     * 栗子:must A , mustB 等于 A&&B
     *
     * should 非必需满足
     * 栗子:must A , should B 等于 (&&A)||B
     *
     * mustNot 必须不满足
     * 栗子:must A , mustNot B 等于 A && !B
     */
    must,
    should,
    mustNot,
}
