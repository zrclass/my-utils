package org.zrclass.excel;

import java.lang.annotation.*;

/**
 * @author zhourui
 * @module
 * @Date 2021/11/12/13:34
 * @description 自定义注解，结合导出工具类ExelExportUtil使用，通过在需要导出的实体类属性上加上此注解，
 * 定义好列名称，实现注解驱动导出的功能
 * 导出列的顺序是根据实体类中属性字段从上到下排列(后期添加sort优化)
 */
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExportKey {

    /**
     *
     * isExport:(标记读入参数)
     */
    boolean isExport() default true;
    /**
     * 字段名称:(名称)
     */
    String remark();

}
