package com.agilesolutions.poc.rest;

import com.agilesolutions.poc.service.HealthService;
import com.azure.spring.cloud.feature.management.FeatureManager;
import com.azure.spring.cloud.feature.management.web.FeatureGate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.Format;
import java.util.Properties;

import static java.lang.String.format;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HelloWorldController {

    private final HealthService healthService;

    private final FeatureManager featureManager;

    @Autowired(required = false)
    @Qualifier("podInfoLabels")
    private Properties podInfoLabels;

    @GetMapping("/hello")
    public String sayHello() {

        String deploymentVersion = (String) podInfoLabels.get("app");

        log.info("**** deployment {} and Feature Beta switch on = {}",deploymentVersion, featureManager.isEnabledAsync("Beta").block());

        return format("Deployment version %s with Feature Beta switch on = %s", deploymentVersion, featureManager.isEnabledAsync("Beta").block());
    }

    @GetMapping("/unhealthy")
    public String unhealthly() {

        log.info("switching health status to DOWN");

        healthService.unhealthy();

        return format("status switch to unhealthy for pod version {}",(String) podInfoLabels.get("app"));
    }

    @GetMapping("/healthy")
    public String healthly() {


        log.info("switching health status to UP");


        healthService.healthy();

        return format("status switch to healthy for pod version {}",(String) podInfoLabels.get("app"));
    }

    @FeatureGate(feature = "Beta", fallback = "/oldFeature")
    @GetMapping("/newFeature")
    public String newFeature() {


        log.info("new Beta version is switch on {}", featureManager.isEnabledAsync("Beta").block());


        healthService.healthy();

        return format("Feature Beta switch on {}", featureManager.isEnabledAsync("Beta").block());
    }

    @GetMapping("/oldFeature")
    public String oldFeature() {


        log.info("old version because Beta is switch on {}", featureManager.isEnabledAsync("Beta").block());


        healthService.healthy();

        return format("old version enabled because Beta switch on = {}", featureManager.isEnabledAsync("Beta").block());
    }



}
