package org.zkit.maven.plugin.mybatis.plus;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
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
import java.util.HashMap;
import java.util.Map;

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
                            .pathInfo(Collections.singletonMap(OutputFile.xml, basedir + "/" + config.getMapperPackage() + "/" + module.getName()));
                    builder.entity("entity.dto");
                })
                .injectionConfig(injectConfig -> {
                    // 添加自定义属性，可以在模板中使用
                    Map<String, Object> customMap = new HashMap<>();
                    customMap.put("moduleName", module.getName());
                    customMap.put("basePackage", config.getBasePackage());
                    customMap.put("baseResultMap", true);
                    customMap.put("baseColumnList", true);
                    injectConfig.customMap(customMap);
                    
                    // 生成基础的 XXBaseMapper.java 文件（替代默认的 mapperBuilder）
                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("BaseMapper.java") // 文件名，会变成 AccountBaseMapper.java
                            .templatePath("templates/base-mapper.java.ftl") // 使用专门的基础 mapper 模板
                            .packageName("mapper.base") // 放在 mapper.base 包下
                            .enableFileOverride() // 允许文件覆盖，保持与之前 mapperBuilder 的策略一致
                            .build());
                    
                    // 生成基础的 XXBaseMapper.xml 文件（替代默认的 mapperBuilder）
                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("BaseMapper.xml") // 文件名，会变成 AccountBaseMapper.xml
                            .templatePath("templates/base-mapper.xml.ftl") // 使用专门的基础 mapper xml 模板
                            .filePath(basedir + "/" + config.getMapperPackage() + "/" + module.getName() + "/base") // 使用配置的mapper路径/base
                            .enableFileOverride() // 允许文件覆盖，保持与之前 mapperBuilder 的策略一致
                            .build());
                    
                    // 生成扩展的 XXMapper.java 文件（继承 base.XXMapper）
                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("Mapper.java") // 文件名，会变成 AccountMapper.java
                            .templatePath("templates/custom-mapper.java.ftl") // Java 接口模板
                            .packageName("mapper") // 放在 mapper 包下
                            .build());
                    
                    // 生成扩展的 XXMapper.xml 文件
                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("Mapper.xml") // 文件名，会变成 AccountMapper.xml
                            .templatePath("templates/custom-mapper.xml.ftl") // 自定义模板路径
                            .filePath(basedir + "/" + config.getMapperPackage() + "/" + module.getName()) // 使用配置的mapper路径
                            .build());
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .enableChainModel()
                            .enableLombok()
                            .enableFileOverride();
                    builder.serviceBuilder().formatServiceFileName("%sService");
                    builder.controllerBuilder().enableRestStyle();
                    // 禁用默认的 mapperBuilder，使用 CustomFile 替代
                    builder.mapperBuilder().disable();
                    String[] tables = module.getTables();
                    builder.addInclude(tables); // 设置需要生成的表名
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
