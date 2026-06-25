package com.featureflags.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMappingConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream()
                .map(element -> modelMapper().map(element, targetClass))
                .toList();
    }

}
