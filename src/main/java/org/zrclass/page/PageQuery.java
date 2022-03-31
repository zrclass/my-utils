package org.zrclass.page;

import java.lang.annotation.*;

/**
 * @module
 * @Author zhourui
 * @Date 2021/08/04/17:15
 * @description
 * 1.此分页仅针对与代码从上至下的第一个列表查询做分页
 * 2.使用此注解必须保证分页参数名称为pageNum,pageSize,否则请将此注解的分页参设置
 * 3.如果分页参数在对象中，对象必须继承PageLimitVo,并且指定传递的对象参数名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PageQuery {
    /**
     * 页号的参数名
     * @return
     */
    String pageNum() default "pageNum";

    /**
     * 每页行数的参数名
     * @return
     */
    String pageSize() default "pageSize";

    /**
     * 分页参数所在参数类的名称,此类必须继承PageLimitVo
     * @return
     */
    String pageArgName() default "";
}
