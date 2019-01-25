package cn.hans.common.conf;

import cn.hans.common.framework.mongo.BigDecimalConverter;
import cn.hans.common.framework.mongo.DatastoreFactoryBean;
import cn.hans.common.framework.mongo.MongoFactoryBean;
import cn.hans.common.framework.mongo.MorphiaFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author  hans
 * Created by  hans on 2017/12/15.
 */
@Configuration
public class SpringMongoConfig {

    @Value("${mongo.host}")
    private String[] serverStrings;

    @Value("${mongo.user}")
    private String username;

    @Value("${mongo.pwd}")
    private String password;

    @Value("${mongo.authDb}")
    private String authDb;

    @Value("${mongo.tooldb}")
    private String toolDb;


    @Value("${mongo.readsec}")
    private boolean readSecondary;

    @Bean("mongoClient")
    public MongoFactoryBean mongoClient() {
        MongoFactoryBean factoryBean = new MongoFactoryBean();
        factoryBean.setServerStrings(serverStrings);
        factoryBean.setAuthDb(authDb);
        factoryBean.setUsername(username);
        factoryBean.setPassword(password);
        factoryBean.setReadSecondary(readSecondary);

        return factoryBean;
    }

    @Bean
    public MorphiaFactoryBean morphiaFactoryBean() {
        MorphiaFactoryBean morphiaFactoryBean = new MorphiaFactoryBean();
        String[] arrays = new String[1];
        arrays[0] = "com.mrwind.delivery.model";

        morphiaFactoryBean.setMapPackages(arrays);
        morphiaFactoryBean.setTypeConverter(new BigDecimalConverter());

        return morphiaFactoryBean;
    }

    @Bean("tool")
    public DatastoreFactoryBean toolBean(@Autowired MongoFactoryBean mongoFactoryBean,
                                             @Autowired MorphiaFactoryBean morphiaFactoryBean) {
        return createDataStore(mongoFactoryBean,morphiaFactoryBean,toolDb);
    }

    /**
     * 根据库民动态创建mongo实例
     * @param mongoFactoryBean          {@link MongoFactoryBean}
     * @param morphiaFactoryBean        {@link MorphiaFactoryBean}
     * @param dbNameAlias               库名
     * @return                          {@link DatastoreFactoryBean}
     */
    public DatastoreFactoryBean createDataStore(@Autowired MongoFactoryBean mongoFactoryBean,
                                                @Autowired MorphiaFactoryBean morphiaFactoryBean,
                                                String dbNameAlias){
        DatastoreFactoryBean factoryBean = new DatastoreFactoryBean();
        try {
            factoryBean.setMorphia(morphiaFactoryBean.getObject());
            factoryBean.setMongo(mongoFactoryBean.getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        factoryBean.setDbName(dbNameAlias);
        factoryBean.setToEnsureCaps(false);

        return factoryBean;
    }
}
