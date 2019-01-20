package cn.hans.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author  hans
 * Created by  hans on 2017/10/24.
 */
public class FieldFilter {

    /**需要处理的对象*/
    private Object object;
    /**过滤的字段集合*/
    private Set<String> fieldSet;
    /**保留标识*/
    private boolean retain = true;

    private FieldFilter(Object object) {
        this.fieldSet = new HashSet<>();
        this.object = object;
    }

    /**
     * 设置要处理的对象
     * @param object
     * @return
     */
    public static FieldFilter object(Object object) {
        return new FieldFilter(object);
    }

    public FieldFilter setObject(Object object) {
        this.object = object;
        return this;
    }

    /**
     * 声明需要处理的的字段(默认保留)
     *
     * @param fieldNames 字段名称
     * @return 需要保留地
     */
    public FieldFilter select(String... fieldNames) {
        Collections.addAll(fieldSet, fieldNames);
        return this;
    }

    /**
     * 根据字段筛选后的结果创建一个JSON对象
     *
     * @return
     */
    public JSON buildResult() {
        JSONObject result = new JSONObject();
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldSet.contains(fieldName) == retain) {
                    result.put(fieldName,field.get(object));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 反转，将保留字段设置为剔除字段
     * 或将剔除字段设置为保留字段。
     *
     * @return
     */
    public FieldFilter reverse() {
        retain = !retain;
        return this;
    }

}
