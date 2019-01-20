package cn.hans.common.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  hans
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeStrategy {

    int strategy();

    /**
     * 在创建时插入当前时间
     */
    int CURRENT_TIME_ON_CREATE = 0;

    /**
     * 在创建和更新时插入当前时间
     */
    int CURRENT_TIME_ON_UPDATE = 1;
}
