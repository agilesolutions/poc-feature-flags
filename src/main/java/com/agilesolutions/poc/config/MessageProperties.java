package com.agilesolutions.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
@Data
public class MessageProperties {
    private String message;
}