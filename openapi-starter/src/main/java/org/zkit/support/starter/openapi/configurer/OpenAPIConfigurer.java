package org.zkit.support.starter.openapi.configurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
        if(StringUtils.isNotEmpty(configuration.getServer())) {
            List<Server> servers = List.of(new Server().url(configuration.getServer()));
            openAPI.servers(servers);
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
            // log.info(e.getMessage());
        }
        if(html == null) {
            return configuration.getDescription();
        }
        return html;
    }

}
