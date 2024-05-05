package org.zkit.support.cloud.starter.code;

public enum ResultCode {

    SUCCESS(0),
    FAIL(500);

    public final int code;

    ResultCode(int code){
        this.code = code;
    }

}
