package com.test.task.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthProperties {
    private String tokenSecret;
    private long tokenExpirationMsec;
    private long refreshExpirationMsec;
}
