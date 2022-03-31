package org.zrclass.redis;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zrclass.dto.Result;
import org.zrclass.utils.RandomUtil;

import java.lang.reflect.Method;

/**
 * @module
 * @Author zhourui
 * @Date 2021/09/08/18:00
 * @description 仿Spring cache自定义一个缓存实现
 */
@Aspect
@Component
public class RedisCacheAop {

    @Autowired
    private  RedisService redisService;

    @Pointcut("@annotation(org.zrclass.redis.SelfRedisCache)")
    private void cache() {

    }

    @Around(value = "cache()")
    public Object SelfRedisCache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getMethod().getReturnType();
        Method method = signature.getMethod();
        //获取注解
        SelfRedisCache selfRedisCache = method.getAnnotation(SelfRedisCache.class);
        //参数名获取
        String[] names = signature.getParameterNames();
        //参数值获取
        Object[] args = joinPoint.getArgs();
        // 拼接key
        String key = getRedisKey(selfRedisCache, args, names);
        // 调用目标方法前查询缓存
        // 先判断目标key是否存在
        if (redisService.hasKey(key)) {
            // 缓存中有数据，查询缓存
            Object result = redisService.get(key);
            return result;
        } else {
            // 缓存不存在,走查询逻辑
            Object result = joinPoint.proceed();
            // 判断查询结果是否为空，防止缓存击穿：为空设置保存空值到缓存，并设置极短的过期时间
            boolean emptyFlag = checkResult(returnType, result);
            if (emptyFlag) {
                // 为空,设置10s的缓存过期时间来防止缓存击穿
                redisService.set(key, result, 10L);
                return result;
            }
            // 判断是否设置过期时间
            if (selfRedisCache.expireFlag()) {
                // 设置过期时间
                long seconds = selfRedisCache.seconds();
                if (seconds > 0) {
                    redisService.set(key, result, selfRedisCache.seconds());
                } else if (seconds == -1) {
                    //  24 小时过期
                    redisService.set(key, result, 60 * 60 * 24L);
                } else {
                    // 0 -5 分钟随机过期时间
                    redisService.set(key, result, RandomUtil.getRandomForLongBounded2(0L, 5 * 60L));
                }
            } else {
                // 不设置过期时间
                redisService.set(key, result);
            }
            return result;
        }
    }

    private boolean checkResult(Class<?> returnType, Object result) {
        boolean emptyFlag = false;
        if (returnType == Result.class) {
            Result results = (Result) result;
            if (results != null && results.isOk()) {
                emptyFlag = results.getData() == null;
            }
        } else {
            emptyFlag = result == null;
        }
        return emptyFlag;
    }

    private String getRedisKey(SelfRedisCache selfRedisCache, Object[] args, String[] names) {
        String key = "";
        String preKey = selfRedisCache.preKey();
        key = key + preKey;
        boolean flag = selfRedisCache.argKeyFlag();
        if (flag) {
            String argName = selfRedisCache.argKeyName();
            String argKey = "";
            for (int i = 0; i < names.length; i++) {
                if (argName.equals(names[i])) {
                    argKey = args[i] + "";
                }
            }
            if (StringUtils.isNotEmpty(argKey)) {
                key = key + "::" + argKey;
            }
        }
        return key;
    }
}
