package com.example.scheduler;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springaicommunity.mcp.security.server.config.McpServerOAuth2Configurer.mcpServerOAuth2;

@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer) {
        return http -> http.with(mcpServerOAuth2(),
                auth -> auth.authorizationServer(issuer));
    }
}

@Service
class DogAdoptionScheduler {

    @McpTool(description = """
            schedule an appointment to pick up or adopt a dog from a 
            Pooch Palace location
            """)
    DogAdoptionScheduleResult schedule(@McpToolParam(description = "the id of the dog") int id,
                                       @McpToolParam(description = "the name of the dog") String name) {
        var user = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        var i = Instant
                .now()
                .plus(3, ChronoUnit.DAYS);
        var sc = new DogAdoptionScheduleResult(name, i, user);
        IO.println("scheduling " + id + '/' + name + " scheduled at " + sc);
        return sc;
    }
}

record DogAdoptionScheduleResult(String name, Instant when, String client) {
}