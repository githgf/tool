package cn.hans.tool.dao.mongo;

import cn.hans.tool.model.mongo.UserActionLog;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/4/28.
 */
@Component
public class UserActionLogDao extends BaseMorphiaDao<UserActionLog> {

    @Autowired
    public UserActionLogDao(@Qualifier("tool") Datastore ds) {
        super(ds);
    }

}
