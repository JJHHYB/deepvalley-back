package jjhhyb.deepvalley.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.production.url}")  //https URL
    private String productionServerUrl;

    @Value("${swagger.development.url}")  //http URL
    private String developmentServerUrl;

    @Bean
    public OpenAPI openAPI() {

        Server localServer = new Server(); // 로컬 서버 설정
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local server");

        Server productionServer = new Server(); // 운영 서버 설정(https)
        productionServer.setUrl(productionServerUrl);
        productionServer.setDescription("Production server");

        Server developmentServer = new Server(); // 개발 서버 설정(http)
        developmentServer.setUrl(developmentServerUrl);
        developmentServer.setDescription("Development server");

        Info info = new Info()
                .title("DeepValley Server API")
                .version("v1.0.0")
                .description("DeepValley 프로젝트의 RestAPI 문서입니다");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer, developmentServer))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
