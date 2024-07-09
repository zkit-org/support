package org.zkit.support.starter.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gateway.internal")
@Data
public class InternalConfiguration {

    private String pattern;
    private Boolean enabled;

}
