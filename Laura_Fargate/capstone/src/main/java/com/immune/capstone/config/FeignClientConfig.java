package com.immune.capstone.config;

import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

}
