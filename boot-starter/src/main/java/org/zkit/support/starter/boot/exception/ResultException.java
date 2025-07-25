package org.zkit.support.starter.boot.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResultException extends RuntimeException {
    private Integer code;
    private String message;
    private Object data;

    public ResultException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public static ResultException internal() {
        return new ResultException(500, "Internal Server Error");
    }
    public static ResultException busy() {
        return new ResultException(500, "Service is busy");
    }
    public static ResultException notfound() {
        return new ResultException(404, "Not found");
    }
    public static ResultException unauthorized() {
        return new ResultException(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    public static ResultException of(Integer code, String message) {
        return new ResultException(code, message);
    }

    public static ResultException of(Integer code, String message, Object data) {
        ResultException resultException = new ResultException(code, message);
        resultException.setData(data);
        return resultException;
    }
}
