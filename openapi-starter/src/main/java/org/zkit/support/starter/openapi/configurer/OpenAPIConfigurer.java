package org.zkit.support.starter.openapi.configurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenAPIConfigurer {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("SpringDoc API Test")
                .description("SpringDoc Simple Application <b>Test</b>")
                .version("0.0.1");
        return new OpenAPI().info(info);

    }

}
