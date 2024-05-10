package org.zkit.support.starter.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email.alidm")
@Data
public class AliDMConfiguration {

    private String regionId;
    private String accountName;
    private String accessKeyId;
    private String accessKeySecret;

}
