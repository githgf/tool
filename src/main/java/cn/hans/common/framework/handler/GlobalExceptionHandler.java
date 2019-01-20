package cn.hans.common.framework.handler;

import cn.hans.common.bean.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 全局异常捕捉
 * @author  hans
 * Created by  hans on 2018/5/4.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @Resource
//    private ErrorLogDao errorLogDao;

    @Resource(name = "logExecutor")
    private TaskExecutor taskExecutor;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result runtimeExceptionHandler(Exception ex) {
        logger.error("捕捉到系统异常",ex);
        /*ErrorLog errorLog = new ErrorLog();
        errorLog.setMessage(ex.getMessage());
        errorLog.setExClassName(ex.getClass().getName());

        taskExecutor.execute(() -> errorLogDao.save(errorLog));*/

        return Result.other("Error: " +ex.getMessage());
    }
}
