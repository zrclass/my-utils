package org.zrclass.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @module
 * @Author zhourui
 * @Date 2021/09/08/18:00
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SelfRedisCache {
    //设定key业务前缀
    String preKey();

    //是否超时设置
    boolean expireFlag() default true;

    //超时时间,默认5分钟; -1表示 24小时过期
    long seconds() default 5*60;

    //是否使用请求中的某个唯一性的参数来作为业务唯一key
    boolean argKeyFlag() default false;

    // argKeyFlag为true时必须指定业务唯一key
    String argKeyName() default "";
}
