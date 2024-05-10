package org.zkit.support.starter.boot.entity;

import lombok.Data;
import org.zkit.support.starter.boot.code.PublicCode;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private Boolean success;
    private String message;
    private T data;

    public boolean isSuccess() {
        return code == PublicCode.SUCCESS.code;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setData(data);
        result.setSuccess(true);
        result.setCode(0);
        return result;
    }

    public static <T> Result<T> result(int code){
        return result(code, null, null);
    }

    public static <T> Result<T> result(int code, T data, String message){
        Result<T> result = new Result<T>();
        result.setData(data);
        result.setSuccess(code == PublicCode.SUCCESS.code);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(code);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(int code, T data) {
        return error(code, "error", data);
    }

    public static <T> Result<T> error(String message, T data) {
        return error(PublicCode.FAIL.code, message, data);
    }

    public static <T> Result<T> error(T data) {
        return error(PublicCode.FAIL.code, "error", data);
    }

}
