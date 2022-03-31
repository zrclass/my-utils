package org.zrclass.dto;

/**
 * @module
 * @Author zhourui
 * @Date 2021/08/24/12:27
 */
public enum ErrorCodeEnum {

    PARAM_INVALID(10001,"传入参数无效")
    ;
    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    ErrorCodeEnum() {
    }
}
