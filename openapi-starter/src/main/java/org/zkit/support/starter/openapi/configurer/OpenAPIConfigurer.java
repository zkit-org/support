package org.zkit.support.starter.openapi.configurer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkit.support.starter.openapi.configuration.OpenAPIConfiguration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
public class OpenAPIConfigurer {

    @Resource
    private OpenAPIConfiguration configuration;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title(configuration.getTitle())
                .description(getDescription())
                .version(configuration.getVersion());
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(info);
        
        // 配置服务器 URL
        if(StringUtils.isNotEmpty(configuration.getServer())) {
            List<Server> servers = List.of(new Server().url(configuration.getServer()));
            openAPI.servers(servers);
        }
        
        // 配置安全认证
        if(configuration.isEnableSecurity()) {
            // 创建安全组件
            Components components = new Components();
            
            // 添加 Bearer Token 安全方案
            SecurityScheme bearerAuth = new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat(configuration.getBearerFormat())
                    .description(configuration.getSecuritySchemeDescription());
            
            components.addSecuritySchemes(configuration.getSecuritySchemeName(), bearerAuth);
            openAPI.components(components);
            
            // 添加全局安全要求
            SecurityRequirement securityRequirement = new SecurityRequirement();
            securityRequirement.addList(configuration.getSecuritySchemeName());
            openAPI.addSecurityItem(securityRequirement);
        }
        
        return openAPI;
    }

    private String getDescription() {
        String html = null;
        try {
            String fileName = Objects.requireNonNull(OpenAPIConfigurer.class.getResource("/OPENAPI.md")).getFile();
            Path path = Paths.get(fileName);
            List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
            String markdown = String.join("\n", allLines);
            Parser parser = Parser.builder().build();
            Node document = parser.parse(markdown);
            html = HtmlRenderer.builder().build().render(document);
        }catch (Exception e) {
            log.warn("read OPENAPI.md error, {}", e.getMessage());
        }
        if(html == null) {
            return configuration.getDescription();
        }
        return html;
    }

}
