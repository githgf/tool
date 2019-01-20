package cn.hans.tool.dao.mongo;


import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

@FunctionalInterface
public interface UpdateHandler<T> {

    /**
     * Mongo更新
     * @param query
     * @param ops
     * @return
     */
    UpdateResults update(Query<T> query, UpdateOperations<T> ops);
}
