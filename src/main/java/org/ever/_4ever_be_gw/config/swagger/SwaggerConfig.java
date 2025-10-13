package org.ever._4ever_be_gw.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.mvc.servlet.path:}")
    private String servletPath;

    @Value("${app.swagger.external-base-path:${spring.mvc.servlet.path:}}")
    private String swaggerExternalBasePath;

    private final Environment environment;

    public SwaggerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI openAPI() {
        String basePath = (swaggerExternalBasePath == null || swaggerExternalBasePath.isBlank()) ? "" : swaggerExternalBasePath.trim();

        Server localServer = new Server()
                .url("http://localhost:" + serverPort + basePath)
                .description("Local Server");

        Server prodServer = new Server()
                .url("https://api.everp.co.kr" + basePath)
                .description("Production Server");

        boolean isProd = false;
        for (String profile : environment.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(profile)) {
                isProd = true;
                break;
            }
        }

        List<Server> servers = isProd ? List.of(prodServer, localServer) : List.of(localServer, prodServer);

        return new OpenAPI()
                .info(apiInfo())
                .servers(servers);
    }

    private Info apiInfo() {
        return new Info()
                .title("4Ever Gateway Service API")
                .description("4Ever 프로젝트 게이트웨이 서비스 REST API 문서입니다.")
                .version("1.0.0");
    }
}
