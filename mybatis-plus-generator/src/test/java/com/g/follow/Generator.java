package com.g.follow;

/**
 * @author sb
 * @date 2023/5/25 15:13
 */

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;


public class Generator {

    public static void main(String[] args) throws IOException {
        //1. 创建代码生成器对象，执行生成代码操作
        Properties prop = new Properties();

        /**
         * 第一步：  使用代码生成器
         */
        //怎么用代码生成器呢？
        //    1.创建代码生成器对象
        //    2.执行代码生成器    //mp包里面的AutoGenerator
//        AutoGenerator autoGenerator = new AutoGenerator();
//        //    告诉它怎么生成，在哪生成
//        //datasource数据源配置
//        DataSourceConfig dataSource = new DataSourceConfig();
//        dataSource.setDriverName(prop.getProperty("jdbc.driverClass"));
//        dataSource.setUrl(prop.getProperty("jdbc.url"));
//        dataSource.setUsername(prop.getProperty("jdbc.user"));
//        dataSource.setPassword(prop.getProperty("jdbc.password"));
//        autoGenerator.setDataSource(dataSource);
        //会在D盘生成一个com文件，但是这个位置是不对的，需要我们再进一步配置
        // 数据源配置
        String userDir = System.getProperty("user.dir");
        String projectDir = userDir + "/mybatis-plus-generator";
        prop.load(new FileInputStream(new File(projectDir + "/src/test/resources/generatorConfig.properties")));

        // 生成的表名
//        String table = "u_member";
//        String table = "b_asset";
//        String table = "b_asset_flow";

        String table = "sys_dict_data";

        // 基于子目录
//        String subModule = table.replaceFirst("tb_", "").replaceFirst("gms_", "");
//        String subModule = "plugins";
//        String subModule = "contract";
//        String subModule = "evm";
//        String subModule = "test";
        String subModule = "tron";
        // 是否有逻辑删除字段
        String deletedCol = "deleted";
        // 首次建议用main，后面建议用test，这样不会覆盖
        String env = "main";
//        String packageName = "com.ywl.ankstake";
//        String packageName = "com.yw";
//        String packageName = "com.gpthk.follow";
//        String packageName = "com.ywlx.game";
        String packageName = "com.cs.copy";
//        String env = "test" ;
        FastAutoGenerator.create(prop.getProperty("jdbc.url"), prop.getProperty("jdbc.user"), prop.getProperty("jdbc.password"))
                .globalConfig(builder -> {
                    builder.author("gpthk")        // 设置作者
                            .enableSpringdoc()        // 开启 swagger 模式 默认值:false
//                            .enableSwagger()        // 开启 swagger 模式 默认值:false
                            .disableOpenDir()       // 禁止打开输出目录 默认值:true
                            .commentDate("yyyy-MM-dd") // 注释日期
                            .dateType(DateType.ONLY_DATE)   //定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                            .outputDir(projectDir + String.format("/src/%s/java", env)); // 指定输出目录
                })

                .packageConfig(builder -> {
//                    builder.parent("com.gpthk.follow") // 父包模块名
                    builder.parent(packageName) // 父包模块名
                            .controller("server.controller")   //Controller 包名 默认值:controller
                            .entity("api.entity")           //Entity 包名 默认值:entity
                            .service("api.service")         //Service 包名 默认值:service
                            .serviceImpl("server.service.impl")
                            .mapper("server.mapper")           //Mapper 包名 默认值:mapper
                            .xml("server.mapper")
//                            .other("model")
                            //.moduleName("xxx")        // 设置父包模块名 默认值:无
                            .moduleName(subModule)
//                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectDir + String.format("/src/%s/resources/mapper", env)))
                            ; // 设置mapperXml生成路径
                    //默认存放在mapper的xml下
                })

//                .injectionConfig(consumer -> {
//                    Map<String, String> customFile = new HashMap<>();
//                    // DTO、VO
//                    customFile.put("DTO.java", "/templates/entityDTO.java.ftl");
//                    customFile.put("VO.java", "/templates/entityVO.java.ftl");
//
//                    consumer.customFile(customFile);
//                })

                .strategyConfig(builder -> {

                    builder.addInclude(table) // 设置需要生成的表名 可边长参数“user”, “user1”
                            .addTablePrefix("tb_", "gms_", "bi_", "tu_", "t_", "a_", "b_", "u_", "s_", "bsc_", "c_", "cp_", "sys_","o_","p_","z_","app_","f_") // 设置过滤表前缀
                            .serviceBuilder()//service策略配置
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .entityBuilder()// 实体类策略配置
//                            .idType(IdType.ASSIGN_ID)//主键策略  雪花算法自动生成的id
//                            .addTableFills(new Column("create_time", FieldFill.INSERT)) // 自动填充配置
//                            .addTableFills(new Property("update_time", FieldFill.INSERT_UPDATE))
                            .enableLombok() //开启lombok
                            .logicDeleteColumnName(deletedCol)
//                            .enableTableFieldAnnotation()// 属性加上注解说明
                            .controllerBuilder() //controller 策略配置
                            .enableRestStyle()
//                            .formatFileName("%sController")
//                            .enableRestStyle() // 开启RestController注解
                            .mapperBuilder()// mapper策略配置
                            .formatMapperFileName("%sMapper")
                            .enableMapperAnnotation()//@mapper注解开启
//                            .formatXmlFileName("%sMapper")
                    ;

                })


                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                // 不生成controller，则把模板置空
//                .templateConfig(builder->builder.controller(""))
//                .templateEngine(new EnhanceFreemarkerTemplateEngine())
                .execute();
    }
}


