package com.supportdesk.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountShieldOpenApi() {

        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()

                .info(
                        new Info()
                                .title("Order Ledger API")
                                .description("""
                                         Order Processing and Transaction History API
                                        
                                                                          Features:
                                                                          - Product catalog management
                                                                          - Order creation with inventory checks
                                                                          - Order status flow with audit history
                                                                          - Coupon-based discounts
                                        """)
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Order ledger Team")
                                                .email("support@orderledger.com")
                                )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()

                                                .name("Authorization")

                                                .type(SecurityScheme.Type.HTTP)

                                                .scheme("bearer")

                                                .bearerFormat("JWT")
                                )
                );

    }

}