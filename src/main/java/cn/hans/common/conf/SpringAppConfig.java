package cn.hans.common.conf;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author  hans
 */
@Configuration
public class SpringAppConfig {

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
