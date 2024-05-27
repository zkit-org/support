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

}
