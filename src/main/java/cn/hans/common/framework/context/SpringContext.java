package cn.hans.common.framework.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;

/**
 * spring 容器
 * @author  hans
 * Created by  hans on 2017/12/7.
 */
@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static ThreadLocal<CurrentContext> threadLocal = new NamedThreadLocal<>("CurrentContext");

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static void setContext(CurrentContext currentContext) {
        threadLocal.set(currentContext);
    }

    public static Date getDate() {
        CurrentContext currentContext = threadLocal.get();
        if (currentContext == null) {
            return new Date();
        } else {
            return currentContext.getCurrentDate();
        }
    }

    public static Timestamp getTimestamp() {
        CurrentContext currentContext = threadLocal.get();
        if (currentContext == null) {
            return new Timestamp(System.currentTimeMillis());
        } else {
            return currentContext.getCurrentTimestamp();
        }
    }

    public static HttpServletRequest getRequest() {
        return threadLocal.get().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return threadLocal.get().getResponse();
    }

    public static void removeContext() {
        threadLocal.remove();
    }
}
