package uk.gov.defra.datareturns.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HalRestTemplate extends RestTemplate {
    public HalRestTemplate() {
        final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .findModulesViaServiceLoader(true)
                .modulesToInstall(new Jackson2HalModule())
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        final MappingJackson2HttpMessageConverter halConverter = new TypeConstrainedMappingJackson2HttpMessageConverter(ResourceSupport.class);
        halConverter.setObjectMapper(objectMapper);
        halConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json")));

        // Build a message converter list with the hal converter explicitly added first!
        final List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(halConverter);
        converters.addAll(super.getMessageConverters());
        super.setMessageConverters(converters);
    }
}
