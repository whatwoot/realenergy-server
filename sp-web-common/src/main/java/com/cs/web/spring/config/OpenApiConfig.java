package com.cs.web.spring.config;

import com.cs.web.spring.prop.OpenApiProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sb
 * @date 2023/5/8 10:46
 */

@Slf4j
@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
@ConditionalOnProperty(value = "sp.openapi.enable", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {

    @OpenAPIDefinition(
            security = @SecurityRequirement(name = "Authorization")
    )
    @SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization", scheme = "Authorization", in = SecuritySchemeIn.HEADER)
    @Configuration(proxyBeanMethods = false)
    @Slf4j
    public static class AutoConfig {

        @Bean
        @ConditionalOnMissingBean
        public OpenAPI springOpenAPI(OpenApiProperties prop) {
            log.info("springOpenAPI init {}", prop);
            return new OpenAPI()
                    .info(new Info()
                            .title(prop.getTitle())
                            .description(prop.getDesc())
                            .version(prop.getVersion())
                    );
        }

        @Bean
        @ConditionalOnMissingBean
        public OpenApiCustomiser openApiCustomiser() {
            log.info("openApiCustomiser init");
            return openApi -> {
                Paths paths = openApi.getPaths();
                Collection<PathItem> values = paths.values();
                for (PathItem pathItem : values) {
                    List<Operation> operations = pathItem.readOperations();
                    //
                    for (Operation operation : operations) {
                        HeaderParameter headerParameter = new HeaderParameter();
                        //headerParameter.setRequired(true);
                        headerParameter.setName("Accept-Language");
                        Map<String, Example> map = new LinkedHashMap<>();
                        Example example = new Example();
                        example.setValue("zh-CN");
                        example.setSummary("中文简体");
                        map.put("zh-CN", example);

                        example = new Example();
                        example.setValue("zh-TW");
                        example.setSummary("中文繁体");
                        map.put("zh-TW", example);

                        example = new Example();
                        example.setValue("en-US");
                        example.setSummary("英语");
                        example.setValueSetFlag(true);
                        map.put("en-US", example);

                        example = new Example();
                        example.setValue("ja-JP");
                        example.setSummary("日语");
                        map.put("ja-JP", example);

                        example = new Example();
                        example.setValue("ko-KR");
                        example.setSummary("韩语");
                        map.put("ko-KR", example);

                        example = new Example();
                        example.setValue("vi-VN");
                        example.setSummary("越南语");
                        map.put("vi-VN", example);

                        headerParameter.setExamples(map);
                        headerParameter.setDescription("国际化参数");
                        operation.addParametersItem(headerParameter);

                        HeaderParameter signHeader = new HeaderParameter();
                        signHeader.setName("sign");
                        signHeader.setDescription("签名值:非生产环境可以用test跳过签名校验");
                        signHeader.setSchema(new StringSchema()
                                .example("test"));

                        operation.addParametersItem(signHeader);

                        HeaderParameter timestampHeader = new HeaderParameter();
                        //headerParameter.setRequired(true);
                        timestampHeader.setName("timestamp");
                        timestampHeader.setDescription("unix时间戳：秒级");
                        operation.addParametersItem(timestampHeader);
                    }
                }
            };
        }
    }
}

