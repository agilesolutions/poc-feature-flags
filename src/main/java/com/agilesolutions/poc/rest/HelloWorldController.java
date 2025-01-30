package com.agilesolutions.poc.rest;

import com.agilesolutions.poc.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired (required = false)
    @Qualifier ("podInfoAnnotations")
    private Properties podInfoAnnotations;

    @Value("${MYSQL_USERNAME}")
    private String username;

    @Value("${MYSQL_PASSWORD}")
    private String password;

    @Value("${LABEL}")
    private String label;


    @GetMapping("/hello")
    public String sayHello() {

        String deploymentVersion = (String) podInfoLabels.get("app");

        log.info("**** deployment {}",deploymentVersion);

        return format("Deployment version %s and Username %s and password %s and finally downWard label %s", deploymentVersion, username, password, label);
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


}
