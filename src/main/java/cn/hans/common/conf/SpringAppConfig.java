package cn.hans.common.conf;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author  hans
 * Created by  hans on 2017/12/21.
 */
@Configuration
public class SpringAppConfig {

//    /**
//     * 跨域配置
//     * @return
//     */
//    @Bean
//    public FilterRegistrationBean appCorsFilter() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new CorsFilter());
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setOrder(1);
//
//        return registrationBean;
//    }

//    /**
//     * 国际化信息源
//     * @return
//     */
//    @Bean("messageSource")
//    public ResourceBundleMessageSource messageSource() {
//        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//        messageSource.setBasenames("message");
//        messageSource.setDefaultEncoding("UTF-8");
//        return messageSource;
//    }

    /**
     * 通用弹性线程池
     * @return
     */
    @Bean("cachedExecutor")
    public TaskExecutor cachedExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(0);
        // 设置最大线程数
        executor.setMaxPoolSize(64);
        // 设置队列容量
        executor.setQueueCapacity(0);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("cached-pool-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务专用线程池
     * @return
     */
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        return fixedExecutor("task-pool-",2);
    }

    /**
     * 请求日志专用线程池
     * @return
     */
    @Bean("logExecutor")
    public TaskExecutor logExecutor() {
        return fixedExecutor("log-pool-",1);
    }



    private TaskExecutor fixedExecutor(String namePrefix,int threadNum) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(threadNum);
        // 设置最大线程数
        executor.setMaxPoolSize(threadNum);
        // 设置队列容量
        executor.setQueueCapacity(Integer.MAX_VALUE);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(0);
        // 设置默认线程名称
        executor.setThreadNamePrefix(namePrefix);
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化线程池
        executor.initialize();
        return executor;
    }

}
