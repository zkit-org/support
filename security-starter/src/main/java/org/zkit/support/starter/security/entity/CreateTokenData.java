package org.zkit.support.starter.security.entity;

import lombok.Data;

@Data
public class CreateTokenData {

    private Long id;
    private String username;
    private Long expiresIn;

}
