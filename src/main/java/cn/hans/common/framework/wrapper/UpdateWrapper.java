package cn.hans.common.framework.wrapper;

import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * @author  hans
 */
public class UpdateWrapper<T> implements UpdateOperations{

    private UpdateOperations<T> updateOperations;

    private boolean isUpdate = false;

    public UpdateWrapper(UpdateOperations<T> updateOperations) {
        this.updateOperations = updateOperations;
    }

    public UpdateOperations<T> ops() {
        return updateOperations;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    @Override
    public UpdateOperations set(String field, Object value) {
        if (value == null) {
            return this;
        }
        updateOperations.set(field,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations setOnInsert(String field, Object value) {
        if (value == null) {
            return this;
        }
        updateOperations.setOnInsert(field,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations add(String fieldExpr, Object value) {
        updateOperations.add(fieldExpr,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations add(String fieldExpr, Object value, boolean addDups) {
        updateOperations.add(fieldExpr,value,addDups);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations addAll(String fieldExpr, List values, boolean addDups) {
        updateOperations.addAll(fieldExpr,values,addDups);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations dec(String field) {
        updateOperations.dec(field);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations disableValidation() {
        updateOperations.disableValidation();
        return confirmUpdate();
    }

    @Override
    public UpdateOperations enableValidation() {
        updateOperations.enableValidation();
        return confirmUpdate();
    }

    @Override
    public UpdateOperations inc(String field) {
        updateOperations.inc(field);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations inc(String field, Number value) {
        updateOperations.inc(field,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations isolated() {
        updateOperations.isolated();
        return confirmUpdate();
    }

    @Override
    public UpdateOperations max(String field, Number value) {
        updateOperations.max(field,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations min(String field, Number value) {
        updateOperations.min(field,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations removeAll(String field, Object value) {
        updateOperations.removeAll(field,value);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations removeAll(String field, List values) {
        updateOperations.removeAll(field,values);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations removeFirst(String field) {
        updateOperations.removeFirst(field);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations removeLast(String field) {
        updateOperations.removeLast(field);
        return confirmUpdate();
    }

    @Override
    public UpdateOperations unset(String field) {
        updateOperations.unset(field);
        return confirmUpdate();
    }

    private UpdateOperations confirmUpdate() {
        isUpdate = true;
        return this;
    }
}
