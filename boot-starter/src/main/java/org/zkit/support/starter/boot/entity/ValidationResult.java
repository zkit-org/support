package org.zkit.support.starter.boot.entity;

import lombok.Data;

@Data
public class ValidationResult {

    private String field;
    private String message;
    private String code;

}
