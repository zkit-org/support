package org.zkit.support.starter.boot.entity;

import lombok.Data;

import java.util.Map;

@Data
public class EmailData {

    private String email;
    private String subject;
    private String template;
    private Map<String, Object> data;

}
