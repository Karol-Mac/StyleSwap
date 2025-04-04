package com.restapi.styleswap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("hal+json", MediaType.parseMediaType("application/hal+json"))
                .mediaType("hal+forms+json", MediaType.parseMediaType("application/hal+forms+json"));
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.parseMediaType("application/hal+json"));
        mediaTypes.add(MediaType.parseMediaType("application/hal+forms+json"));
        converter.setSupportedMediaTypes(mediaTypes);
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // określ endpointy, które mają obsługiwać CORS
                .allowedOrigins("https://styleswap-691724339754.us-central1.run.app/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // określ dozwolone metody
                .allowedHeaders("*") // określ dozwolone nagłówki
                .allowCredentials(true); // pozwala na wysyłanie ciasteczek (jeśli potrzebujesz)
    }
}