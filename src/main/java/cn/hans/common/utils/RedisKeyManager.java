package cn.hans.common.utils;

import cn.hans.common.constant.CommonConstant;

/**
 * @author  hans
 * 管理redis中所有key的类
 */
public class RedisKeyManager {

    private final static String VER_CODE_KEY = "wf_express_%s_verCode_%s";

    private final static String USER_INFO_KEY = "wf_express_%s_token_%s";

    private final static String MOBILE_KEY = "wf_express_%s_mobile_%s";

    private final static String KEY_ID_KEY = "wf_express_%s_token_%s_keyId";

    private final static String API_CLIENT_KEY = "wf_express_%s__apiclient_jc_token";

    private final static String CLIENT_AUTH_CODE_KEY = "wf_express_%s_clientAuthCode_%s";

    private final static String DELIVERED_SMS_KEY = "wf_express_%s_deliveredsms_%s";

    private final static String TASK_REDIS_LOCK_KEY="wf_express_%s_task_lock_%s";

    private final static String POINT_LIST_KEY = "wf_openapi_pointList_%s_%s_%s";

    private final static String ASSIGN_NUM_KEY = "wf_openapi_assignNum_%s_%s_%s";

    private final static String METHOD_LOCK_KEY = "wf_express_%s_userId_%s_method_%s";

    /**
     * 获取verCode对应的key
     * @return 转换后的key
     */
    public static String getVerCodeKey(String verCode) {
        return String.format(VER_CODE_KEY, CommonConstant.CITY,verCode);
    }

    /**
     * 获取UserInfo相关的key
     * @param token token
     * @return      转换后的key
     */
    public static String getUserInfoKey(String token) {
        return String.format(USER_INFO_KEY, CommonConstant.CITY,token);
    }

    /**
     * 获取mobile相关的key
     * @param mobile 电话号码
     * @return       转换后的key
     */
    public static String getMobileKey(String mobile) {
        return String.format(MOBILE_KEY, CommonConstant.CITY,mobile);
    }

    /**
     * 获取keyId相关的key
     * @param token  token
     * @return       转换后的key
     */
    public static String getKeyIdKey(String token) {
        return String.format(KEY_ID_KEY, CommonConstant.CITY,token);
    }

    /**
     * 获取apiClient相关的key
     * @return 转换后的key
     */
    public static String getApiClientKey() {
        return String.format(API_CLIENT_KEY, CommonConstant.CITY);
    }

    /**
     * 获取clientAuthCode相关的key
     * @param authCode 校验码
     * @return         转换后的key
     */
    public static String getClientAuthCodeKey(String authCode) {
        return String.format(CLIENT_AUTH_CODE_KEY, CommonConstant.CITY,authCode);
    }

    /**
     * 获取配送短信对应的key
     * @param expressNo 运单号
     * @return          转换后的key
     */
    public static String getDeliveredSmsKey(String expressNo) {
        return String.format(DELIVERED_SMS_KEY, CommonConstant.CITY, expressNo);
    }

    /**
     * 获取task定时任务的redis锁对应的key
     * @param taskMethodName
     * @return
     */
    public static String getTaskRedisLockKey(String taskMethodName){
        return String.format(TASK_REDIS_LOCK_KEY,CommonConstant.CITY,taskMethodName);
    }


    /**
     * 获取今天或者明天已经接收点位列表对应的key
     * @param userId 用户id
     * @param date   日期
     * @return       转换后的key
     */
    public static String getPointListKey(String userId,String date) {
        return String.format(POINT_LIST_KEY,CommonConstant.CITY, userId, date);
    }

    /**
     * 获取分配数量对应的key
     * @param userId 用户id
     * @param date   日期
     * @return       转换后的key
     */
    public static String getAssignNumKey(String userId,String date) {
        return String.format(ASSIGN_NUM_KEY,CommonConstant.CITY, userId, date);
    }

    /**
     *  获取方法并发锁key
     * @param userId    用户id
     * @param method    方法名
     * @return
     */
    public static String getMethodLockKey(String userId,String method){
        return String.format(METHOD_LOCK_KEY,CommonConstant.CITY,userId,method);
    }
}
