package ch.admin.bit.jme.cdct.task.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Module Task Rest API",
                description = "Task Rest API for the CDCT Provider Example",
                version = "1.0.0"
        ),
        security = {@SecurityRequirement(name = "OIDC")}
)
@Configuration
class SwaggerConfigTask {

    @Bean
    GroupedOpenApi taskApi() {
        return GroupedOpenApi.builder()
                .group("task-api")
                .pathsToMatch("/api/task/**")
                .build();
    }
}
