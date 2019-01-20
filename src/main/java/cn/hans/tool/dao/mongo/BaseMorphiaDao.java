package cn.hans.tool.dao.mongo;

import cn.hans.common.framework.annotation.TimeStrategy;
import cn.hans.common.framework.context.SpringContext;
import cn.hans.common.framework.wrapper.UpdateWrapper;
import cn.hans.common.utils.CollectionUtil;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateException;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * mongo morphia框架扩展类
 * 1.支持对象id已存在时对非空字段的更新操作
 * 2.解决了更新时更新字段为空时的异常
 * 3.UpdateOperations类未设置更新属性时将不进行更新操作，以避免数据被抹去的风险
 * 4.支持 Date 类型字段的自动生成和更新策略
 * @author hans
 */
public class BaseMorphiaDao<T> extends BasicDAO<T, ObjectId> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> onUpdateTimeFields = new ArrayList<>();

    public BaseMorphiaDao(Datastore ds) {
        super(ds);
    }

    @PostConstruct
    public void validateTimeStrategy() {
        // 校验注解字段类型的合法性
        Class<T> entityClass = getEntityClass();
        for (Field field : entityClass.getDeclaredFields()) {
            TimeStrategy timeStrategy = field.getAnnotation(TimeStrategy.class);
            if (timeStrategy != null) {
                if (field.getType() != Date.class) {
                    throw new UpdateException("Invalid type for @TimeStrategy: "
                            + field.getName() + ",which must be Date");
                }
                if (timeStrategy.strategy() == TimeStrategy.CURRENT_TIME_ON_UPDATE) {
                    onUpdateTimeFields.add(field.getName());
                }
            }
        }
    }

    /**
     * 根据Id更新类属性
     * @param entity 对象
     * @return       更新个数
     */
    @SuppressWarnings("unchecked")
    public int insertOrUpdateById(T entity) {
        if (entity == null)return 0;
        Query<T> query = createQuery();
        UpdateOperations<T> update = createUpdateOperations();

        for (Field field : entity.getClass().getDeclaredFields()) {
            //过滤静态字段
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String fieldName = field.getName();
            Id id = field.getAnnotation(Id.class);
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (id != null) {
                //id为空则插入新值
                if (value == null) {
                    save(entity);
                    return 1;
                }
                query.field(fieldName).equal(new ObjectId((String)value));
            } else {
                if (value != null) {
                    update.set(fieldName,value);
                }
            }
        }

        return this.updateFirst(query,update).getUpdatedCount();
    }

    /**
     * 根据某个字段更新属性
     * @param entity    对象
     * @param fieldName 字段名
     * @return          更新个数
     */
    @SuppressWarnings("unchecked")
    public int updateByField(T entity, String fieldName) {
        if (entity == null)return 0;
        Query<T> query = createQuery();
        UpdateOperations<T> update = createUpdateOperations();

        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
                    String name = field.getName();
                    Id id = field.getAnnotation(Id.class);
                    field.setAccessible(true);
                    Object value;
                    try {
                        value = field.get(entity);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    if (name.equals(fieldName)) {
                        if (value == null) {
                            throw new RuntimeException(String.format("The value of '%s' is null",fieldName));
                        }
                        query.field(fieldName).equal(value);
                    } else {
                        if (value != null && id == null) {
                            update.set(name,value);
                        }
                    }
                });

        return update(query,update).getUpdatedCount();
    }


    @Override
    public UpdateOperations<T> createUpdateOperations() {
        return new UpdateWrapper<>(super.createUpdateOperations());
    }

    @Override
    public Key<T> save(T entity) {
        if (entity == null)return null;
        Date date = new Date();
        handleOnCreateTime(entity,date);
        return super.save(entity);
    }

    @Override
    public Key<T> save(T entity, WriteConcern wc) {
        if (entity == null)return null;
        Date date = new Date();
        handleOnCreateTime(entity,date);
        return super.save(entity, wc);
    }

    private void handleOnCreateTime(T entity,Date date) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(TimeStrategy.class) != null)
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        field.set(entity, date);
                    } catch (IllegalAccessException e) {
                        logger.error("save error",e);
                    }
                });
    }

    @Override
    public UpdateResults update(Query<T> query, UpdateOperations<T> ops) {
        return handleUpdate(query,ops,super::update);
    }

    @Override
    public UpdateResults updateFirst(Query<T> query, UpdateOperations<T> ops) {
        return handleUpdate(query,ops,super::updateFirst);
    }

    private UpdateResults handleUpdate(Query<T> query, UpdateOperations<T> ops, UpdateHandler<T> updateHandler) {
        for (String field : onUpdateTimeFields) {
            ops.set(field,SpringContext.getDate());
        }

        if (ops instanceof UpdateWrapper) {
            UpdateWrapper<T> updateWrapper = (UpdateWrapper<T>) ops;
            if (!updateWrapper.isUpdate()) {
                return new UpdateResults(WriteResult.unacknowledged());
            }
            return updateHandler.update(query,updateWrapper.ops());
        }

        return updateHandler.update(query,ops);
    }

    /**
     * 批量插入
     * @param entities 插入集合
     */
    public void batchInsert(Collection<T> entities) {
        if (CollectionUtil.isEmpty(entities))return;
        Date date = SpringContext.getDate();
        entities.forEach(entity -> handleOnCreateTime(entity,date));
        super.getDs().insert(entities);
    }
}
