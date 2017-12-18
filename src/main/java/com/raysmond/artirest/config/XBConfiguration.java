package com.raysmond.artirest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XBConfiguration {

    public void setNumber(Integer number) {
        this.number = number;
    }

    private Integer number;


    @Bean
    public Integer getNumber(){
        return number;
    }
}
