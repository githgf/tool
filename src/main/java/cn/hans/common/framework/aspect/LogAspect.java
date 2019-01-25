package cn.hans.common.framework.aspect;

import cn.hans.tool.dao.mongo.UserActionLogDao;
import cn.hans.tool.model.mongo.UserActionLog;
import com.alibaba.fastjson.JSON;
import cn.hans.common.framework.context.SpringContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * UserActionLog日志切面
 * @author  hans
 */
@Component
@Aspect
@Order(0)
public class LogAspect {

    private Log logger = LogFactory.getLog(this.getClass());

    @Resource
    private UserActionLogDao userActionLogDao;

    @Resource(name = "logExecutor")
    private TaskExecutor taskExecutor;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMappingPointCut() {

    }

    @AfterReturning(value = "requestMappingPointCut()", returning = "retVal")
    public void doLogController(JoinPoint joinpoint, Object retVal) {
        String className = joinpoint.getTarget().getClass().getName();
        if (!className.startsWith("com")) {
            return;
        }
        String methodName = joinpoint.getSignature().getName();
        if(methodName.toLowerCase().equals("gocurrentflownode")){
            return ;
        }
        HttpServletRequest request = SpringContext.getRequest();

        StringBuilder paramBuilder = new StringBuilder();
        for(int i = 0; i < joinpoint.getArgs().length; i++){
            if(joinpoint.getArgs()[i] != null){
                String name = joinpoint.getArgs()[i].getClass().getName();
                if(name.contains("org.apache.catalina.connector")
                        ||  "org.springframework.web.multipart.commons.CommonsMultipartFile".equals(name)
                        || name.contains("org.springframework")){
                    continue;
                }
                paramBuilder.append(JSON.toJSONString(joinpoint.getArgs()[i]));
            }
        }

        UserActionLog userActionLog = new UserActionLog();
        long startTime = (long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();

        userActionLog.setSpendTime(endTime - startTime);
        userActionLog.setFunctionName(methodName);
        userActionLog.setControllerName(className);
        userActionLog.setParamValue(paramBuilder.toString());
        if (retVal != null) {
            userActionLog.setResponseValue(retVal.toString());
        }
        userActionLog.setCreateTime(new Date());

        taskExecutor.execute(() -> {
            if (logger.isDebugEnabled()) {
                logger.debug(userActionLog);
            }
            userActionLogDao.save(userActionLog);
        });
    }
}
