package org.zkit.support.starter.security.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthConfiguration {

    private List<String> commonAccess;
    private List<String> publicAccess;

}
