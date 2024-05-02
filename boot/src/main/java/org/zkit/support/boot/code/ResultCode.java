package org.zkit.support.boot.code;

public enum ResultCode {

    SUCCESS(0),
    FAIL(999),

    ACCESS_LIMIT(4031);

    public final int code;

    ResultCode(int code){
        this.code = code;
    }

}
