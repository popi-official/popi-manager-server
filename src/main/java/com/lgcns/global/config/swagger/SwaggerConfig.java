package com.lgcns.global.config.swagger;

import static com.lgcns.global.common.constants.UrlConstants.DEV_SERVER_URL;
import static com.lgcns.global.common.constants.UrlConstants.LOCAL_SERVER_URL;
import static com.lgcns.global.helper.SpringEnvironmentHelper.DEV;

import com.lgcns.global.helper.SpringEnvironmentHelper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SwaggerConfig {

    private final SpringEnvironmentHelper springEnvironmentHelper;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("PoPI Manager Server API")
                                .description("PoPI Manager Server API 명세서입니다.")
                                .version("v0.0.1"))
                .servers(getSwaggerServers())
                .components(authSetting())
                .addSecurityItem(securityRequirement());
    }

    private List<Server> getSwaggerServers() {
        return List.of(new Server().url(getServerUrlByProfile()));
    }

    private String getServerUrlByProfile() {
        return switch (springEnvironmentHelper.getCurrentProfile()) {
            case DEV -> DEV_SERVER_URL;
            default -> LOCAL_SERVER_URL;
        };
    }

    private Components authSetting() {
        return new Components()
                .addSecuritySchemes(
                        "accessToken",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"));
    }

    private SecurityRequirement securityRequirement() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("accessToken");
        return securityRequirement;
    }
}
