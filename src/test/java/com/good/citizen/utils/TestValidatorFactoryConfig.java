package com.good.citizen.utils;

import com.good.citizen.config.PatchFieldValueExtractor;
import com.good.citizen.shared.TimeMachine;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Configuration;

/**
 * For validation purposes we set current time to constant.
 * <p>
 * This configuration class allows the use of @Future or @FutureOrPresent annotations for validation.
 * Without this configuration, reference time for validation would change each time test are run.
 * This might lead to unexpected behaviour.
 */
//@TestConfiguration
public class TestValidatorFactoryConfig extends LocalValidatorFactoryBean {

    @Override
    protected void postProcessConfiguration(Configuration<?> configuration) {
        super.postProcessConfiguration(configuration);
        configuration.addValueExtractor(new PatchFieldValueExtractor());
        configuration.clockProvider(TimeMachine.getClockProvider());
    }
}