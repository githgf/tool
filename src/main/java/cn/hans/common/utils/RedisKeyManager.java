package cn.hans.common.utils;

import cn.hans.common.constant.CommonConstant;
import cn.hans.common.framework.context.SpringContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author  hans
 * 管理redis中所有key的类
 */
public class RedisKeyManager {

    private final static String METHOD_LOCK_KEY = "wf_express_%s_userId_%s_method_%s";

    /**
     *  获取方法并发锁key
     * @param userId    用户id
     * @param method    方法名
     * @return
     */
    public static String getMethodLockKey(String userId,String method){
        return String.format(METHOD_LOCK_KEY,CommonConstant.CITY,userId,method);
    }

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 将数据存入redis缓存中(设置过期时间)
     * @param key key
     * @param data 数据
     * @param timeout 过期时间
     * @param timeUnit
     * @return
     */
    public boolean putValue(String key, Object data, long timeout, TimeUnit timeUnit){
        if (data != null && StringUtils.isNotBlank(key)){

            redisTemplate.opsForValue().set(key,data,timeout,timeUnit);

            return true;
        }else{
            return false;
        }

    }

    /**
     * 将数据存入redis缓存中，
     * @param key
     * @param data
     * @return
     */
    public boolean putValue(String key,Object data){
        if (data != null && StringUtils.isNotBlank(key)){
            redisTemplate.opsForValue().set(key,data);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 从redis中获取数据
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 使用lua脚本方式设置锁
     * @param key           redis-key
     * @param timeout       过期时间
     * @return              redis-value
     * <p>
     *     这种方式的好处就是类似事务的原子性，因为脚本执行要么全部成功要么全部失败，而且是一起执行的
     * </p>
     */
    public String setLockByScript(String key,long timeout){
        if (StringUtils.isNotBlank(key)  && timeout > 0){
            long time = SpringContext.getDate().getTime();
            String script = "return redis.call('set',KEYS[1],ARGV[1],'EX'," + timeout +",'NX')";
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(script);
            redisScript.setResultType(String.class);
            String execute = redisTemplate.execute(redisScript, new StringRedisSerializer(),new StringRedisSerializer(), Collections.singletonList(key), String.valueOf(time));
            if (StringUtils.isNotBlank(execute) && execute.toLowerCase().equals("ok")){
                return String.valueOf(time);
            }
        }
        return null;
    }

    public String setLock(String key,long timeout,TimeUnit timeUnit){
        if (StringUtils.isNotBlank(key)){
            try {
                long l = System.currentTimeMillis();
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(l));
                if (ifAbsent != null && ifAbsent){
                    Boolean expire = redisTemplate.expire(key, timeout, timeUnit);
                    if (expire != null && expire){
                        return (String) get(key);
                    }
                }else {
                    Long aLong = (Long) get(key);
                    //锁过期未释放
                    if (System.currentTimeMillis() - aLong > 10 * 1000){
                        redisTemplate.delete(key);
                    }
                }
            } catch (Exception e) {
                redisTemplate.delete(key);
            }
        }
        return null;
    }

    public String setLockBySet(String key,long timeout,TimeUnit timeUnit){
        if (StringUtils.isNotBlank(key)){
            String uuid = UUIDUtils.getUUID();
            redisTemplate.opsForValue().set(key, uuid,timeout,timeUnit);
            Object o = get(key);
            if (o != null && StringUtils.isNotBlank(o.toString()) && uuid.equals(o.toString())){
                return o.toString();
            }
        }

        return null;
    }

    /**
     *  脚本方式释放锁
     * @param key       redis-key
     * @param data      redis-value
     */
    public void releaseLockByScript(String key,String data){
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(
                "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1]) > 0 and 'ok' or 'fail'\n" +
                        "else\n" +
                        "    return 'fail'\n" +
                        "end");
        redisScript.setResultType(String.class);
        String execute = redisTemplate.execute(redisScript, new StringRedisSerializer(),new StringRedisSerializer(), Collections.singletonList(key), data);
        System.out.println(execute);
    }


}
