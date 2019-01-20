package cn.hans.common.utils;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具
 * @author  hans
 * Created by  hans on 2017/12/28.
 */
public class CollectionUtil extends CollectionUtils {

    /**
     * 将集合中的成员按照某个字段映射并生成相应的map
     * @param collection 归类对象
     * @param function   字段获取的方法
     * @param <K>        字段类别
     * @param <T>        成员类型
     * @return           map结果集
     */
    public static <K,T> Map<K,T> convertToMapByFunc(Collection<T> collection, Function<T,K> function) {
        return collection.stream().filter(t -> null != function.apply(t)).collect(Collectors.toMap(function,Function.identity(),(entity1,entity2) -> entity1));
    }

    /**
     * 将集合中的成员按照某个字段归类并生成相应的map
     * @param collection 归类对象
     * @param function   字段获取的方法
     * @param <K>        字段类别
     * @param <T>        成员类型
     * @return           map结果集
     */
    public static <K,T> Map<K,List<T>> groupToMapByFunc(Collection<T> collection, Function<T,K> function) {
        if (isEmpty(collection))return new HashMap<>();
        // 注意：如果function的结果为null，则报错element cannot be mapped to a null key,
        return collection.stream().filter(t -> null != function.apply(t)).collect(Collectors.groupingBy(function,Collectors.toList()));
    }

    /**
     * 用于提取对象数组中的字段数组
     * @param collection 提取对象
     * @param function  字段名称
     * @param <T>        成员类型
     * @param <E>        字段类型
     * @return           set结果集
     */
    public static <T,E> Set<E> mapFieldsToSet(Collection<T> collection, Function<T,E> function) {
        return collection.stream().filter(t -> null != function.apply(t)).map(function).collect(Collectors.toSet());
    }

    /**
     * 用于提取对象数组中的字段数组
     * @param collection 提取对象
     * @param function  字段名称
     * @param <T>        成员类型
     * @param <E>        字段类型
     * @return           list结果集
     */
    public static <T,E> List<E> mapFieldsToList(Collection<T> collection, Function<T,E> function) {
        return collection.stream().filter(t -> null != function.apply(t)).map(function).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <T,E> void putElement(
            Collection<E> collection,
            Collection<T> queryCollection,
            String fieldName) {
        try {
            for (T t : queryCollection) {
                Field field = t.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object o = field.get(t);
                collection.add((E) o);
            }
        } catch (Exception e) {
            throw new RuntimeException("invalid field or fieldType: " + fieldName);
        }
    }
}
