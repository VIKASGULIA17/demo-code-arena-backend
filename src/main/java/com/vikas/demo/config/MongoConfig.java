package com.vikas.demo.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        // This tells Spring Boot: "Whenever you see an ObjectId, send it as a plain String"
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }
}