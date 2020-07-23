package com.good.citizen.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterRegistryCustomizerConfiguration {

    private final String applicationName = "The good citizen";

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(){
        return registry -> registry.config().commonTags("application", applicationName);
    }
}