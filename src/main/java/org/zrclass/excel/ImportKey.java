package org.zrclass.excel;

import java.lang.annotation.*;

/**
 * @author zhourui
 * @module
 * @Date 2021/12/13/17:00
 * @description 导入注解，配合ExcelImportUtil使用
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImportKey {
    /**
     * 是否对字段进行校验,校验暂功能暂时未实现
     *
     * @return
     */
    boolean isCheck() default false;

    /**
     * @return 校验类名称,校验暂功能暂时未实现
     */
    String checkClassName() default "";

    /**
     * 校验方法名称,校验暂功能暂时未实现
     *
     * @return
     */
    String checkMethodName() default "";

    /**
     * 导入列的顺序
     * @return
     */
    int sort();
}
