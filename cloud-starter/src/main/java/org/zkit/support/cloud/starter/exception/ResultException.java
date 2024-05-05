package org.zkit.support.cloud.starter.exception;

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
}
