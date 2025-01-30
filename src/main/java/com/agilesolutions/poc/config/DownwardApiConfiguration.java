package com.agilesolutions.poc.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class DownwardApiConfiguration {

    @Bean
    public PropertiesFactoryBean podInfoLabels() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new FileSystemResource("/etc/podinfo/labels"));
        return propertiesFactoryBean;
    }

    @Bean
    public PropertiesFactoryBean podInfoAnnotations() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new FileSystemResource("/etc/podinfo/annotations"));
        return propertiesFactoryBean;
    }

}