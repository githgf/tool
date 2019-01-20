package cn.hans.common.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

/**
 *  文件上传配置
 * @author          hans
 */
@Configuration
@EnableConfigurationProperties(MultipartProperties.class)
public class FileUploadConfig {
    private final MultipartProperties multipartProperties;
    public FileUploadConfig(MultipartProperties multipartProperties){
        this.multipartProperties=multipartProperties;
    }
    /**
    * 注册解析器
    * @return      {@link StandardServletMultipartResolver}
    */
    @Bean(name= DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    @ConditionalOnMissingBean(MultipartResolver.class)
    public StandardServletMultipartResolver multipartResolver(){
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        multipartResolver.setResolveLazily(multipartProperties.isResolveLazily());
        return multipartResolver;
    }
    /**
    * 上传配置
    * @return       {@link MultipartConfigElement}
    */
    @Bean
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement(){
        return this.multipartProperties.createMultipartConfig();
    }
}