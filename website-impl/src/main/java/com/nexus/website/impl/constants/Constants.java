package com.nexus.website.impl.constants;

import java.math.BigDecimal;

/**
 * 常量
 */
public class Constants {
    /**常量int类型1**/
    public static final int CONSTANT_ONE_INT = 1;
    /**常量int类型0 **/
    public static final int CONSTANT_ZERO_INT = 0;
    /**缓存过期时间一个月**/
    public final static int REDIS_ONEMONTH = 2592000;//60*60*24*30
    /**缓存过期时间一天**/
    public final static int REDIS_ONEDAY = 86400;//60*60*24
    /**缓存过期时间半天**/
    public final static int REDIS_HALFDAY = 43200;//60*60*12
    /**缓存过期时间十天**/
    public final static int REDIS_TENDAY = 864000;//60*60*24*10
    /**缓存过期时间十分钟**/
    public final static int REDIS_TENMINUTE = 600;//60*10
    /**过期时间为一周**/
    public static final int REDIS_ONEWEEK = 604800;
    /**缓存过期时间三小时**/
    public final static int REDIS_THREEHOUR = 10800;//3*60*60
    /**缓存过期时间一小时**/
    public final static int REDIS_ONEHOUR = 3600;//60*60
    /**缓存过期时间半小时**/
    public final static int REDIS_HALFHOUR = 1800;//60*30
    /**缓存过期时间半分钟**/
    public final static int REDIS_HALFMINUTE = 30;
    /**缓存过期时间90秒**/
    public final static int REDIS_NINETY = 90;
    /**空字符串**/
    public final static String CONSTANT_EMPTY_STR = "";

}
