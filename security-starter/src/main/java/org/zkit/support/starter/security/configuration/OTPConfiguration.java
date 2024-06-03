package org.zkit.support.starter.security.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.otp")
@Data
public class OTPConfiguration {

    private String issuer;
    private int secretSize = 32;
    private String randomNumberAlgorithm = "SHA1PRNG";
    private int windowSize = 1;
    private int secondPerSize = 30;

}
