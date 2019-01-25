package cn.hans.common.constant;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 全局常量
 * @author  hans
 * Created by  hans on 2018/4/23.
 */
@Configuration
public class CommonConstant {



    /**城市简称*/
    public static String CITY = "hangzhou";


    public static String SOURCE_PATH;

    public static String EXPORT_FILE_PATH;

    public static void setSourcePath(){
        try {
            SOURCE_PATH = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setExportFilePath(ApplicationHome applicationHome){
        if (applicationHome != null){
            //如果是单元测试getSource（） 会自动为空 ===> 原因： applicationHome.findSource() 方法 96行
            EXPORT_FILE_PATH = applicationHome.getSource() != null ? applicationHome.getSource().getParentFile().getPath() : SOURCE_PATH;
        }
    }

    @Bean
    public ApplicationHome applicationHome(){

        ApplicationHome applicationHome = new ApplicationHome(CommonConstant.class);

        setSourcePath();
        setExportFilePath(applicationHome);

        return applicationHome;
    }



}
