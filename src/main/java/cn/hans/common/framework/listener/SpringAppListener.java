package cn.hans.common.framework.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author  hans
 * Created by  hans on 2017/10/19.
 */
@Component
public class SpringAppListener implements ApplicationListener<ContextRefreshedEvent> {

    private Log log = LogFactory.getLog(SpringAppListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //sysHelper.redisCacheReload();
        log.info("My application listener started!");
    }
}
