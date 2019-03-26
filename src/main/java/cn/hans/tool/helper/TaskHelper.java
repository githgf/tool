package cn.hans.tool.helper;

import cn.hans.common.utils.JVMUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskHelper {

    @Scheduled(cron = "0/5 * * * * ?")
    public void testJvmPid(){
        System.out.println("Linux pid == " + new JVMUtil().getJvmPIDOnLinux());
    }

}
