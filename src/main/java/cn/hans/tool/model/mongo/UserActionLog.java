package cn.hans.tool.model.mongo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/6/4.
 */


@Entity(noClassnameStored = true)
@Data
public class UserActionLog {
    @Id
    private String userActionId;
    private String controllerName;
    private String functionName;
    private String keyId;
    private String paramValue;
    private String responseValue;
    private String actionRemark;
    private Date createTime;
    private String creatorId;
    private long spendTime;


}
