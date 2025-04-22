package org.zkit.maven.plugin.mybatis.plus;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.zkit.maven.plugin.mybatis.plus.config.GeneratorConfig;
import org.zkit.maven.plugin.mybatis.plus.entity.Datasource;
import org.zkit.maven.plugin.mybatis.plus.entity.Module;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author grant
 * @version 0.0.1
 */
@Mojo(name = "generate")
public class GeneratorMojo extends AbstractMojo {

    @Parameter(name = "basedir", defaultValue = "${basedir}")
    private String basedir;

    public void execute() {
        Log log = getLog();
        GeneratorConfig config = GeneratorConfig.init(basedir, log);
        Module[] modules = config.getModules();
        for(String moduleName : config.getActives()){
            Arrays.stream(modules).filter(module -> module.getName().equals(moduleName)).findFirst().ifPresent(module -> {
                log.info("module: " + moduleName);
                handleModule(config, module);
                log.info("tables: " + Arrays.toString(module.getTables()));
            });
        }
    }

    private void handleModule(GeneratorConfig config, Module module) {
        Datasource datasource = config.getDatasource();
        String username = System.getenv("GEN_DB_USERNAME");
        String password = System.getenv("GEN_DB_PASSWORD");
        FastAutoGenerator.create(datasource.getUrl(), username, password)
                .globalConfig(builder -> {
                    builder.author("generator") // 设置作者
                            .enableSpringdoc()
                            .outputDir(basedir + "/src/main/java") // 指定输出目录
                            .disableOpenDir();
                })
                    .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                        if(metaInfo.getTypeName().equals("TINYINT") && metaInfo.getLength() == 1) {
                            return DbColumnType.BOOLEAN;
                        }
                        if(metaInfo.getTypeName().equals("DATETIME")) {
                            return DbColumnType.DATE;
                        }
                        if(metaInfo.getTypeName().equals("JSON")) {
                            return DbColumnType.OBJECT;
                        }
                        return typeRegistry.getColumnType(metaInfo);
                    }))
                .packageConfig(builder -> {
                    builder.parent(config.getBasePackage()) // 设置父包名
                            .moduleName(module.getName()) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, basedir + "/" + config.getMapperPackage() + "/" + module.getName())); // 设置mapperXml生成路径
                    builder.entity("entity.dto");
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .enableChainModel()
                            .enableLombok()
                            .enableFileOverride();
                    builder.serviceBuilder().formatServiceFileName("%sService");
                    builder.controllerBuilder().enableRestStyle();
                    builder.mapperBuilder()
                            .enableBaseColumnList()
                            .enableBaseResultMap();
                    String[] tables = module.getTables();
                    builder.addInclude(tables); // 设置需要生成的表名
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
