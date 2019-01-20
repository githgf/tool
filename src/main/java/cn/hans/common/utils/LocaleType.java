package cn.hans.common.utils;

import cn.hans.common.framework.context.SpringContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.NamedThreadLocal;

import java.util.Locale;

/**
 * @author  hans
 * Created by  hans on 2017/10/24.
 */
public class LocaleType {

    private static MessageSource messageSource;

    private static Locale locale;

    private static ThreadLocal<Locale> threadLocal = new NamedThreadLocal<>("locale_threadLocal");

    public static void setLocale(Locale locale) {
        threadLocal.set(locale);
    }

    public static String getMessage(String key) {
        if (messageSource == null) {
            messageSource = (MessageSource)SpringContext.getBean("messageSource");
        }

        if (locale == null) {
            locale = SpringContext.getBean(Locale.class);
        }

        try {
            return messageSource.getMessage(key,null,locale);
        } catch (NoSuchMessageException e) {
            return key;
        }
    }

    public static Locale getLocale() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
