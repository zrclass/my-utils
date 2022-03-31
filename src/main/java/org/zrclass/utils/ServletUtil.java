package org.zrclass.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author zhourui
 * @module
 * @Date 2021/12/14/15:49
 * @description 通过RequestContextHolder直接获取HttpServletRequest,HttpServletResponse,无需通过Controller层传参方式而获得HttpServletRequest，HttpServletResponse，
 *  直接使用Spring提供的RequestContextHolder工具类获取；
 *  注意：多线程中，子线程无法使用此工具类获取，可通过主线程获取，通过参数传递到子线程
 */
public class ServletUtil {
    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

}
