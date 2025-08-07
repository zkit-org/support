package org.zkit.support.starter.openapi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openapi")
@Data
public class OpenAPIConfiguration {

    private String title;
    private String description;
    private String version;
    private String server;
    
    /**
     * 是否启用认证功能
     */
    private boolean enableSecurity = false;
    
    /**
     * 认证方案名称
     */
    private String securitySchemeName = "bearerAuth";
    
    /**
     * 认证方案描述
     */
    private String securitySchemeDescription = "JWT Bearer Token";
    
    /**
     * Bearer token 格式
     */
    private String bearerFormat = "JWT";

}
