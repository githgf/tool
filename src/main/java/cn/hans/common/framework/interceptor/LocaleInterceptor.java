package cn.hans.common.framework.interceptor;

import cn.hans.common.utils.LocaleType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author  hans 日志
 * Created by  hans on 2017/8/3.
 */
public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//        Locale locale = httpServletRequest.getLocale();
//        if (locale != null) {
//            LocaleType.setLocale(locale);
//        } else {
//            LocaleType.setLocale(Locale.CHINA);
//        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求完成后清除当前LocalType中的信息,
     * 以防内存泄漏
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//        LocaleType.remove();
    }
}
