package cn.hans.common.framework.interceptor;

import cn.hans.common.framework.context.CurrentContext;
import cn.hans.common.framework.context.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * api拦截器
 * 用于记录日志
 * @author  hans
 */
public class LogInterceptor implements HandlerInterceptor {

	private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
	private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocal StartTime");

	@Override
	public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// 打印JVM信息。
		if (logger.isDebugEnabled()){
			//得到线程绑定的局部变量（开始时间）
			long beginTime = startTimeThreadLocal.get();
			//2、结束时间
			long endTime = System.currentTimeMillis();
			logger.debug("计时结束："+new SimpleDateFormat("hh:mm:ss.SSS").format(endTime)+"  耗时："+ (endTime - beginTime)
					+"ms  URI: "+request.getRequestURI()+"  最大内存: "+Runtime.getRuntime().maxMemory()/1024/1024
					+"m  已分配内存: "+Runtime.getRuntime().totalMemory()/1024/1024+"m  已分配内存中的剩余空间: "+Runtime.getRuntime().freeMemory()/1024/1024
					+"m  最大可用内存: "+(Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024+"m");
			//清除请求的时间信息，以防内存泄漏
			startTimeThreadLocal.remove();
		}
		SpringContext.removeContext();
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object arg2) throws Exception {
		long millis = System.currentTimeMillis();
		if (logger.isDebugEnabled()){
			//线程绑定变量（该数据只有当前请求的线程可见）
			startTimeThreadLocal.set(millis);
			logger.debug("开始计时: "+new SimpleDateFormat("hh:mm:ss.SSS").format(millis)
	        		+"  URI: "+request.getRequestURI());
		}

		CurrentContext currentContext = new CurrentContext();
		currentContext.setRequest(request);
		currentContext.setResponse(response);
		Timestamp timestamp = new Timestamp(millis);
		currentContext.setCurrentTimestamp(timestamp);
		currentContext.setCurrentDate(timestamp);
		SpringContext.setContext(currentContext);
		return true;
	}

}
