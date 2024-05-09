package org.zkit.support.cloud.starter.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StringUtils.isEmpty(token))
            return null;
        return token.replace("Bearer ", "");
    }

}
