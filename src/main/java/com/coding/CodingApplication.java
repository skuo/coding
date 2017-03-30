package com.coding;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@EnableEurekaClient
public class CodingApplication { 

    public static void main(String[] args) {
        SpringApplication.run(CodingApplication.class, args);
    }
    
    @Bean
    // Group all REST endpoint with '/bids' prefix in the same docket
    public Docket bidsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("bids")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/bids.*"))
                .build();
    }

    @Bean
    // Group all REST endpoint with '/hola' prefix in the same docket
    public Docket holaApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("hola")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/hola.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot REST API with Swagger")
                .description("Spring Boot REST API with Swagger")
                .termsOfServiceUrl("http://com.coding")
                .contact(new Contact("Steve Kuo","http://com.coding/skuo","skuo@coding.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com")
                .version("2.0")
                .build();
    }
                
}
