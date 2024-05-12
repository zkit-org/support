package org.zkit.support.starter.boot.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class StringUtils {

    public static boolean isEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}
