package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DatabaseProperties {

    private DataSourceConfig primary; // readWrite
    private DataSourceConfig secondary; // readOnly

    @Data
    public static class DataSourceConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private int maximumPoolSize;

    }
}

