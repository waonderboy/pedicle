package org.example.pedicle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate xmlRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setMessageConverters(List.of(
//                new StringHttpMessageConverter(StandardCharsets.UTF_8),
//                new Jaxb2RootElementHttpMessageConverter()
//        ));
        return restTemplate;
    }
}
