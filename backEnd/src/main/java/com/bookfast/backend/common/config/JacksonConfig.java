package com.bookfast.backend.common.config;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        
        // Increase nesting depth limit to handle complex entity relationships
        // Default is 1000, but we can increase it significantly
        StreamWriteConstraints streamWriteConstraints = StreamWriteConstraints.builder()
                .maxNestingDepth(10000) // Increase nesting depth limit
                .build();
        objectMapper.getFactory().setStreamWriteConstraints(streamWriteConstraints);
        
        // Disable FAIL_ON_EMPTY_BEANS to prevent errors with empty relationships
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // Disable writing dates as timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Ignore unknown properties during deserialization
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return objectMapper;
    }
}

