package org.zkit.support.starter.boot.entity;

import lombok.Data;

@Data
public class CreateTokenData {

    private Long id;
    private String username;
    private Long expiresIn;

}
