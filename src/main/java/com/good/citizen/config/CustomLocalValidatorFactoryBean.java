package com.good.citizen.config;

import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Configuration;

@Component
public class CustomLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

    @Override
    protected void postProcessConfiguration(Configuration<?> configuration) {
        super.postProcessConfiguration(configuration);

        configuration.addValueExtractor(new PatchFieldValueExtractor());
    }
}