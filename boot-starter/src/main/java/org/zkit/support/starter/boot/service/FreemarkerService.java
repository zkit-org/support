package org.zkit.support.starter.boot.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
@Slf4j
public class FreemarkerService {

    public String render(String template, Object data) {
        Version version = new Version("2.3.0");
        Configuration configure = new Configuration(version);
        configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        configure.setClassForTemplateLoading(FreemarkerService.class, "/templates/");
        String result = null;
        try {
            Template temp = configure.getTemplate(template);
            StringWriter writer = new StringWriter();
            temp.process(data, writer);
            result = writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

}
