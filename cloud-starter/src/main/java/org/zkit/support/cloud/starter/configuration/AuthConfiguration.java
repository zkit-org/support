package org.zkit.support.cloud.starter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthConfiguration {

    private String su;
    private String defaultRole;
    private String initPassword;
    private String jwtSecret;
    private String transportPrivateKey;
    private String aesKey;
    private boolean debug;
    private String emailCode;
    private String otpCode;
    private String resetOtpCode;
    private List<String> commonAccess;
    private List<String> publicAccess;

}
