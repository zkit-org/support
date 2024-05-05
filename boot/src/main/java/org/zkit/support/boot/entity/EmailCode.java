package org.zkit.support.boot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EmailCode {

    private String code;
    private Date createdAt;

}
