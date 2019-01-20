package cn.hans.common.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * 结果输出类
 */

public final class Result extends JSONObject {

    private final static String STATUS = "code";
    private final static String MESSAGE = "msg";
    private final static String DATA = "data";

    private final static String SUCCESS_CODE = "1";
    private final static String FAIL_CODE = "-1";
    private final static String HEAD_ERROR_CODE = "-2";
    private final static String OTHER_CODE = "0";

    private final static String SUCCESS_MESSAGE = "success";
    private final static String FAIL_MESSAGE = "illegal request";
    private final static String HEAD_ERROR_MESSAGE = "illegal head";
    private final static String OTHER_MESSAGE = "other error";

    public static Result success() {
        return getResult(SUCCESS_CODE,SUCCESS_MESSAGE,null);
    }

    public static Result success(Object content) {
        return getResult(SUCCESS_CODE,SUCCESS_MESSAGE,content);
    }

    public static Result fail() {
        return getResult(FAIL_CODE,FAIL_MESSAGE,null);
    }

    public static Result fail(String message) {
        return getResult(FAIL_CODE,message,null);
    }

    public static Result expection(String message,String content){
        return getResult(FAIL_CODE,message,content);
    }

    public static Result headError() {
        return getResult(HEAD_ERROR_CODE,HEAD_ERROR_MESSAGE,null);
    }

    public static Result headError(String message) {
        return getResult(HEAD_ERROR_CODE,message,null);
    }

    public static Result other() {
        return getResult(OTHER_CODE,OTHER_MESSAGE,null);
    }

    public static Result other(String message) {
        return getResult(OTHER_CODE,message,null);
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.getString(STATUS));
    }

    private Result() {
    }

    private static Result getResult(String code, String message, Object content) {
        Result result = new Result();
        result.put(STATUS,code);
        result.put(MESSAGE,message);
        if (content != null) {
            result.put(DATA,content);
        }
        return result;
    }

}
