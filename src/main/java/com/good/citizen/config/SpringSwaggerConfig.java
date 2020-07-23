package com.good.citizen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableSwagger2
@Import({springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class})
public class SpringSwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo())
                .genericModelSubstitutes(Optional.class);
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Good citizen",
                "DESCRIPTION",
                "1.0v",
                "TERMS OF SERVICE URL",
                new Contact("NAME", "URL", "EMAIL"),
                "LICENSE",
                "LICENSE URL",
                List.of());
    }
}