package org.zkit.support.starter.boot.code;

public enum PublicCode {

    SUCCESS(0, "public.success"),
    FAIL(500, "public.fail"),

    THROTTLE(10001001, "public.throttle");

    public final int code;
    public final String key;

    PublicCode(int code, String key){
        this.code = code;
        this.key = key;
    }

}
