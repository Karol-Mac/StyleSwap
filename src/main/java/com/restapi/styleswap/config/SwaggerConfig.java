package com.restapi.styleswap.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Karol",
                        email = "karol200242@gmail.com",
                        url = "https://karol-mac.github.io/"
                ),
                title = "StyleSwap API",
                description = "Documentation of StyleSwap API"
        ),
        servers = {
                @Server(
                        url = "https://styleswap-691724339754.us-central1.run.app/",
                        description = "production ENV"
                ),
                @Server(
                        url = "http://localhost:8080",
                        description = "local ENV"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authorization header using the Bearer scheme",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in= SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

}
