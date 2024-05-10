package org.zkit.support.starter.boot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "response-advice")
@Data
public class ResponseAdviceConfiguration {

    private List<String> exclude;

}
