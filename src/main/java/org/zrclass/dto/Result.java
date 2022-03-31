package org.zrclass.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;


public class Result<T> {
    //@ApiModelProperty(value = "0-成功; 1-业务异常; -1: 内部错误")
    private int code;
    //@ApiModelProperty(value = "消息，when code=1，可用于向客户展示; when code=-1, 内部错误, 不对外展示; ")
    private String msg;
    //@ApiModelProperty(value = "一般情况下，仅code=0时，返回有效值; 特殊接口可能也会返回值")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    public boolean isOk() {
        return code == 0;
    }

    public Map<String, Object> toMap() {
        return MapResult.of(code, msg, data);
    }

    public static <T> Result result(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        if (msg != null) {
            result.setMsg(msg);
        }
        if (data != null) {
            result.setData(data);
        }

        return result;
    }

    public static <T> Result ok(T data) {
        return result(0, null, data);
    }

    public static Result ok() {
        return result(0, null, null);
    }

    public static Result ok(String msg){
        return result(0,msg,null);
    }
    public static Result fail(String msg) {
        return result(1, msg, null);
    }

    public static Result fail() {
        return result(1, "未知的错误", null);
    }

    public static Result fail(int code, String msg) {
        return result(code, msg, null);
    }

    public static String failStr(int code, String msg) {
        if (msg != null) {
            return "{\"code\": " + code + ", \"msg\": \"" + msg + "\"}";
        }

        return "{\"code\": " + code + "}";
    }

    public static String failStr(String msg) {
        return failStr(1, msg);
    }

    public static Result fail(ErrorCodeEnum codeEnum){
        return result(codeEnum.getCode(),codeEnum.getMsg(),null);
    }


    public static <T> ResponseEntity<T> failed(int code, String msg) {
        return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.valueOf(code))
                .body(Result.fail(-1,msg));
    }


    public static <T> ResponseEntity<T> failed(ErrorCodeEnum codeEnum){
        return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.OK.value())
                .body(Result.fail(codeEnum.getCode(),codeEnum.getMsg()));
    }
}
