package com.br.crudestudo.config;

import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SwaggerStartupLogger {

    private final ServletContext servletContext;
    private final Environment environment;
    private final String swaggerPath;

    public SwaggerStartupLogger(ServletContext servletContext,
                                Environment environment,
                                @Value("${springdoc.swagger-ui.path}") String swaggerPath) {
        this.servletContext = servletContext;
        this.environment = environment;
        this.swaggerPath = swaggerPath;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        // "local.server.port" só existe com servidor embutido; em Tomcat externo o host/porta são do servidor
        String port = environment.getProperty("local.server.port");
        String host = port != null ? "http://localhost:" + port : "";
        log.info("Swagger UI disponível em: {}{}{}", host, servletContext.getContextPath(), swaggerPath);
    }
}
