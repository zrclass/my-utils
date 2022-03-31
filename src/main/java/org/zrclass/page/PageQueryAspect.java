package org.zrclass.page;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @module
 * @Author zhourui
 * @Date 2021/08/04/17:16
 * @description 支持service返回值为PageResult和Result两种返回形式,
 * 如果有其他格式的返回类型可尝试修改为支持可扩展的形式（策略模式）
 */
@Aspect
@Component
public class PageQueryAspect {

    @Pointcut("@annotation(org.zrclass.page.PageQuery)")
    private void page() {

    }

    @Around(value = "page()")
    public Object pagingQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getMethod().getReturnType();

        Method method = signature.getMethod();
        //获取注解
        PageQuery pageQuery = method.getAnnotation(PageQuery.class);
        //参数名获取
        String[] names = signature.getParameterNames();
        //参数值获取
        Object[] args = joinPoint.getArgs();
        String pageNum = "";
        String pageSize = "";
        for (int i = 0; i < names.length; i++) {
            if (StringUtil.isNotEmpty(pageQuery.pageArgName())) {
                if (pageQuery.pageArgName().equals(names[i])) {
                    PageLimitVo pageLimitVo = (PageLimitVo) args[i];
                    pageNum = pageLimitVo.getPageNum() + "";
                    pageSize = pageLimitVo.getPageSize() + "";
                }
            } else {
                if (pageQuery.pageNum().equals(names[i])) {
                    pageNum = args[i] + "";
                }
                if (pageQuery.pageSize().equals(names[i])) {
                    pageSize = args[i] + "";
                }
            }
        }
        if (StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
            try {
                PageHelper.startPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
                if (returnType == PageResult.class) {
                    //这里自己实现返回类型，官方自带的返回数据太冗余了
                    PageResult pageResult = (PageResult) joinPoint.proceed();
                    PageInfo<?> pageInfo = new PageInfo<>((List<?>) pageResult.getList());
                    return PageResult.getPage(pageInfo.getTotal(), pageInfo.getList());
                } /*else if (returnType == Result.class) {
                    Result result = (Result) joinPoint.proceed();
                    if (result.isOk()) {
                        PageInfo<?> pageInfo = new PageInfo<>((List<?>) result.getData());
                        return Result.ok(PageResult.getPage(pageInfo.getTotal(), pageInfo.getList()));
                    }
                    return result;
                } else if (returnType == RespResult.class) {
                    RespResult result = (RespResult) joinPoint.proceed();
                    PageInfo<?> pageInfo = new PageInfo<>((List<?>) result.getData());
                    return RespResult.SUCCESS("success", PageResult.getPage(pageInfo.getTotal(), pageInfo.getList()));
                }*/

            } finally {
                //保证线程变量被清除
                if (PageHelper.getLocalPage() != null) {
                    PageHelper.clearPage();
                }
            }
        }
        return joinPoint.proceed();
    }

}

