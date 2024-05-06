package org.zkit.support.cloud.starter.code;

public enum MailCode {

    FAIL(10101001, "mail.fail");

    public final int code;
    public final String key;

    MailCode(int code, String key){
        this.code = code;
        this.key = key;
    }

}
