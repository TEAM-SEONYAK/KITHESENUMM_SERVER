package org.sopt.seonyakServer.global.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:8081", "http://localhost:5173",
                        "https://seonyak.com", "https://www.seonyak.com", "https://api.seonyak.com",
                        "https://seonyak-client.pages.dev", "https://api.seonyak.com/swagger-ui/index.html")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 외부 라이브러리의 코틀린 컨버터를 제거
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(KotlinSerializationJsonHttpMessageConverter.class::isInstance);
    }
}
