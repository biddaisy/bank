package com.softindustry.bank.configuration.secutiry.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("jwt")
@Data
public class JwtProperties {

    private long tokenValidity;
    private String secretKey;

}
