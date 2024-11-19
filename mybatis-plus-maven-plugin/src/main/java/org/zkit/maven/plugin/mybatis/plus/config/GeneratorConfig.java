package org.zkit.maven.plugin.mybatis.plus.config;

import lombok.Data;
import org.apache.maven.plugin.logging.Log;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.zkit.maven.plugin.mybatis.plus.entity.Datasource;
import org.zkit.maven.plugin.mybatis.plus.entity.Module;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
public class GeneratorConfig {
    private String basePackage;
    private String mapperPackage;
    private Datasource datasource;
    private String[] actives;
    private Module[] modules;

    public static GeneratorConfig init(String base, Log log) {
        GeneratorConfig config = new GeneratorConfig();
        try{
            String path = new File(base, "src/main/resources/mybatis-plus/generator.yml").getAbsolutePath();
            log.info("init form: " + path);
            InputStream input = Files.newInputStream(Paths.get(path));
            Yaml yaml = new Yaml(new Constructor(GeneratorConfig.class, new LoaderOptions()));
            config = yaml.load(input);
        }catch (Exception e) {
            log.error("init error: " + e.getMessage(), e);
        }
        return config;
    }
}
