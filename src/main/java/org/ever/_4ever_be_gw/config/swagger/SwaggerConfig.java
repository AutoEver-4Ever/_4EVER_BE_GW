package org.ever._4ever_be_gw.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.mvc.servlet.path:}")
    private String servletPath;

    @Bean
    public OpenAPI openAPI() {
        String basePath = (servletPath == null || servletPath.isBlank()) ? "" : servletPath.trim();
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + basePath)
                                .description("Local Server")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("4Ever Gateway Service API")
                .description("4Ever 프로젝트 게이트웨이 서비스 REST API 문서입니다.")
                .version("1.0.0");
    }
}
