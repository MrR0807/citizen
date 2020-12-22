package com.good.citizen.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@TestConfiguration
public class TestRestTemplateConfiguration {

    /**
     * Problem overview
     * When test are run, a warning is generated (type information is missing for brevity):
     * ```
     * c.j.MappingJackson2HttpMessageConverter : Failed to evaluate Jackson deserialization for type <type>:
     * com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Invalid type definition for type <type>:
     * Argument #0 has no property name, is not Injectable: can not use as Creator
     * ```
     * Explanation:
     * When we test, we use TestRestTemplate. TestRestTemplate underneath registers MessageConverters to delegate message conversion according to their type.
     * In this case, we are interested in MappingJackson2HttpMessageConverter. Spring has a bug, which makes it register two MappingJackson2HttpMessageConverters.
     * One is configured correctly, and includes Jackson ``ParameterNamesModule`` module, the other is not. When RestTemplate tries to parse response, it goes
     * through a list of registered message converters and check whether it can convert that type:
     * ```
     * List<MediaType> allSupportedMediaTypes = getMessageConverters().stream()
     * .filter(converter -> canReadResponse(this.responseType, converter))
     * .flatMap(this::getSupportedMediaTypes)
     * .distinct()
     * .sorted(MediaType.SPECIFICITY_COMPARATOR)
     * .collect(Collectors.toList());
     * ```
     * Badly configured MappingJackson2HttpMessageConverter does not have ParameterNamesModule module, which essentially leads to Converter saying that it can
     * read the response, however, then it fails, due to misconfiguration. The solution is to remove badly configured MappingJackson2HttpMessageConverter
     * from RestTemplate.
     * <p>
     * More resources:
     * In depth explanation: https://blog.trifork.com/2020/05/26/i-used-springs-resttemplate-to-fetch-some-json-and-you-wont-believe-what-happened-next/
     *
     * @read @JsonCreator
     * @read javac -parameters
     * @see RestTemplateCustomizer
     * @see org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter::canRead
     * @see org.springframework.web.client.RestTemplate.AcceptHeaderRequestCallback::doWithRequest
     */
    @Bean
    RestTemplateCustomizer unwantedConvertersRemovingCustomizer() {
        return restTemplate -> {
            boolean foundCorrectJackson2MappingMessageConverter = false;
            for (var iter = restTemplate.getMessageConverters().listIterator(); iter.hasNext(); ) {
                HttpMessageConverter<?> converter = iter.next();
                if (converter instanceof MappingJackson2HttpMessageConverter) {
                    if (foundCorrectJackson2MappingMessageConverter) {
                        iter.remove();
                    } else {
                        var jacksonObjectMapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                        var containsParameterNamesModule = jacksonObjectMapper.getRegisteredModuleIds()
                                .contains("com.fasterxml.jackson.module.paramnames.ParameterNamesModule");
                        if (containsParameterNamesModule) {
                            foundCorrectJackson2MappingMessageConverter = true;
                        }
                    }
                }
            }
        };
    }
}