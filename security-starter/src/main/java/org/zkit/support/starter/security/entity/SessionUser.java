package org.zkit.support.starter.security.entity;

import lombok.Data;

import java.util.List;

@Data
public class SessionUser {

    private Long id;
    private String username;
    private List<String> authorities;
    private List<Api> apis;
    private Long expiresIn;
    private String jwtToken;

    @Data
    public static class Api {
        private String path;
        private String method;
    }

}
