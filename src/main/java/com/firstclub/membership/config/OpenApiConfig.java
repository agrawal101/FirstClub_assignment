package com.firstclub.membership.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata shown on the Swagger UI page. The endpoint documentation itself is generated
 * by springdoc from the controllers and DTOs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI membershipOpenApi() {
        return new OpenAPI().info(new Info()
                .title("FirstClub Membership API")
                .version("1.0.0")
                .description("Subscription membership program with tiered, configurable benefits."));
    }
}
