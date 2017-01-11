package com.coding;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class CodingApplication { 

    public static void main(String[] args) {
        SpringApplication.run(CodingApplication.class, args);
    }
    
    @Bean
    // Group all REST endpoint with '/bids' prefix in the same docket
    public Docket payrollApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("bids")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/bids.*"))
                .build();
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot REST API with Swagger")
                .description("Spring Boot REST API with Swagger")
                .termsOfServiceUrl("http://org.brest")
                .contact("Steve Kuo")
                .license("Apache License Version 2.0")
                .licenseUrl("https://githuh.com")
                .version("2.0")
                .build();
    }
                
}
