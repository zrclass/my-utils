package org.zrclass.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zhourui
 * @module
 * @Date 2021/11/12/13:48
 * @description 反射工具类
 */
@Slf4j
public class ReflectUtil {
    private ReflectUtil() {
    }



    /**
     * 获取字段对应值，并转为String类型，空值返回空字符串
     *
     * @param fieldName 实体类的属性名（方法名）
     * @param obj       实例化的实体类
     * @param format    为属性为日期类型的，格式化字符串 如 yyyy-MM-dd ,如果转入null 默认值为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static synchronized String getStringValue(String fieldName, Object obj, String format) throws ReflectiveOperationException {
        Object objectValue = getValueByGetter(fieldName, obj);
        if (objectValue == null) {
            return "";
        }
        String result = objectValue.toString();
        // 如果类型为BigDecimal,去掉末尾的0
        if (objectValue instanceof BigDecimal) {
            BigDecimal value = (BigDecimal) objectValue;
            value = value.stripTrailingZeros();
            result = value.toPlainString();
        } else if (objectValue instanceof Date) {
            if (format != null && format != "") {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format((Date) objectValue);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                result = sdf.format((Date) objectValue).replace(" 00:00:00", "");
            }
        }

        return result.trim();
    }


    public static List<Object> getValueListByFields(List<String> fieldNames, Object data) {
        List<Object> list  = Lists.newArrayList();
        fieldNames.stream().forEach(fieldName -> {
            try {
                String stringValue = getStringValue(fieldName, data, null);
                list.add(stringValue);
            } catch (ReflectiveOperationException e) {
                log.error("occurs an error when get reflect value",e);
            }
        });
        return list;
    }


    public static Object getValueByGetter(String fieldName, Object obj) throws ReflectiveOperationException {
        Method getter = getGetter(fieldName, obj.getClass());
        if (getter != null) {
            return getter.invoke(obj);
        }

        return null;
    }

    /**
     * @param fieldName set方法名
     * @param obj       实体类 （已实例化）
     * @param fieldVal  set方法需要set的值
     * @param fmt       格式化日期类型 时用到的 参数，这个看实际情况传入 如："yyyy/MM/dd"
     * @return 注意反射invoke调用的set方法 这里没用返回值的 所以直接返回原来的obj即可
     * @throws Exception
     */
    public static Object setValueBySetter(String fieldName, Object obj, Object fieldVal, String fmt) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        Method setter = getSetter(fieldName, obj.getClass());
        if (setter == null) {
            throw new ReflectiveOperationException(fieldName + "--没有该set方法");
        }
        Class<?>[] parameterTypes = setter.getParameterTypes();
        String pType = parameterTypes[0].getName();
        //System.out.println(pType); //java.lang.String  java.util.Date

        if (null != fieldVal && !"".equals(fieldVal)) {
            if (pType.indexOf("String") != -1) {
                setter.invoke(obj, fieldVal);
            } else if (pType.indexOf("Date") != -1) {
                setter.invoke(obj, sdf.parse(fieldVal.toString()));
            } else if (pType.indexOf("Integer") != -1 || pType.indexOf("int") != -1) {
                setter.invoke(obj, Integer.parseInt(fieldVal.toString()));
            } else if (pType.indexOf("Long") != -1 || pType.indexOf("long") != -1) {
                setter.invoke(obj, Long.parseLong(fieldVal.toString()));
            } else if (pType.indexOf("Double") != -1 || pType.indexOf("double") != -1) {
                setter.invoke(obj, Double.parseDouble(fieldVal.toString()));
            } else if (pType.indexOf("Boolean") != -1 || pType.indexOf("boolean") != -1) {
                setter.invoke(obj, Boolean.parseBoolean(fieldVal.toString()));
            } else {
                //System.out.println("not supper type" + fieldType);
                //return null;
            }
        }
        return obj;
    }

    /**
     * 获取get方法
     *
     * @param fieldName
     * @param cls
     * @return
     */
    public static Method getGetter(String fieldName, Class<?> cls) {
        for (Method method : cls.getMethods()) {
            if (method.getName().equalsIgnoreCase("get".concat(fieldName)) && method.getParameterTypes().length == 0) {
                return method;
            }
        }
        return null;
    }

    /**
     * 获取set方法
     *
     * @param fieldName
     * @param cls
     * @return
     */
    public static Method getSetter(String fieldName, Class<?> cls) {
        for (Method method : cls.getMethods()) {
            if (method.getName().equalsIgnoreCase("set".concat(fieldName)) && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过属性名获取Field对象
     *
     * @param fieldName
     * @param cls
     * @return
     */
    public static synchronized Field getFieldByName(String fieldName, Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        if (cls.getSuperclass() != null) {
            return getFieldByName(fieldName, cls.getSuperclass());
        }

        return null;
    }

    /**
     * 通过对象.class获取所有Fields，包括父类
     *
     * @param cls
     * @return
     */
    public static List<Field> listFields(Class<?> cls) {
        Field[] fs = cls.getDeclaredFields();
        List<Field> fields = new ArrayList<>(Arrays.asList(fs));
        if (cls.getSuperclass() != null) {
            fields.addAll(listFields(cls.getSuperclass()));
        }
        return fields;
    }

    public static boolean fieldExist(String fieldName, Class<?> cls) {
        return getFieldByName(fieldName, cls) != null;
    }
}
