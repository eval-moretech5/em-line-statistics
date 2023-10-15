package org.eval.moretech.linestatistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApiConfiguration() {
        return new OpenAPI()
            .addServersItem(new Server().url("https://viewplace.ru/"))
            .addServersItem(new Server().url("https://localhost"))
            .info(new Info()
                .title("Сервис Статистики и прогнозирования очередей")
                .version("1.0")
            );
    }
}
