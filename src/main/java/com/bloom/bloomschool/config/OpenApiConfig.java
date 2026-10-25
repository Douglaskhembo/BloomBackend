package com.bloom.bloomschool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bloomSchoolOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BloomSchool API")
                        .description("REST API for the BloomSchool management system")
                        .version("v1"));
    }
}
