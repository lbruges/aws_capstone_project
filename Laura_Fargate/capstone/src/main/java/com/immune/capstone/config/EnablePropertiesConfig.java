package com.immune.capstone.config;

import com.immune.capstone.config.properties.RulesProperties;
import com.immune.capstone.config.properties.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({S3Properties.class, RulesProperties.class})
public class EnablePropertiesConfig {

}
