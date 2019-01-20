package cn.hans.common.framework.aspect;

import cn.hans.common.bean.Result;
import cn.hans.common.framework.context.SpringContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author  hans
 * 参数校验切片
 */
@Component
@Aspect
@Order(1)
public class ValidationAspect {

    @Pointcut("execution(public * cn.hans.tool.controller..*.*(..))")
    public void validationPointCut(){};

    @Around("validationPointCut()")
    public Object valid(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = SpringContext.getRequest();
        request.setAttribute("startTime",System.currentTimeMillis());
        // requestBody 参数校验
        for (Object o : joinPoint.getArgs()) {
            if (o instanceof BindingResult) {
                BindingResult result = (BindingResult) o;
                if (result.hasErrors()) {
                    String failMsg = "参数有误";
                    return Result.fail(failMsg);
                }
            }
        }
        return joinPoint.proceed();
    }

}
