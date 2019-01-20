package cn.hans.common.framework.aspect;

import cn.hans.common.utils.CodeUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hans.common.bean.Result;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *  全局异常处理
 */
@Component
@Order(2)
@Aspect
public class ThrowableAspect {
    @Pointcut("execution(public * cn.hans.tool.controller..*.*(..))")
    public void validationPointCut(){};

    @Around("validationPointCut()")
    public Object throwableHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
            return proceed;
        } catch (Throwable throwable) {
            if (throwable instanceof Exception){
                Exception exception = (Exception) throwable;
                exception.printStackTrace();
            }
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            JSONObject throwableObj = new JSONObject();

            throwableObj.put("cause",throwable.getCause() != null ? throwable.getCause().getMessage() : null);

            JSONArray stackTraceArray = new JSONArray();
            for (StackTraceElement stackTraceElement : stackTrace) {
                String className = stackTraceElement.getClassName();

                if (StringUtils.isNotBlank(className)
                        && className.contains("cn.hans")
                        && !CodeUtils.isSpecialChar(className)){
                    JSONObject stackTraceElementObj = new JSONObject();
                    stackTraceElementObj.put("className",stackTraceElement.getClassName());
                    stackTraceElementObj.put("methodName",stackTraceElement.getMethodName());
                    stackTraceElementObj.put("lineNumber",stackTraceElement.getLineNumber());

                    stackTraceArray.add(stackTraceElementObj);
                }
            }
            throwableObj.put("stackTrace",stackTraceArray);
            return Result.expection(throwable.getClass().getName(),throwableObj.toString());
        }
    }
}
