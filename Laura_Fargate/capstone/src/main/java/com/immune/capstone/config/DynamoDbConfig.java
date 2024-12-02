package com.immune.capstone.config;

import com.immune.capstone.config.properties.DynamoDbProperties;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(DynamoDbProperties.class)
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(DynamoDbProperties properties, AwsCredentialsProvider awsCredentialsProvider) {
        var clientBuilder = DynamoDbClient.builder();

        String testOverride = properties.getEndpointOverride();
        if (StringUtils.isNotBlank(testOverride)) {
            clientBuilder.endpointOverride(URI.create(testOverride));
        }

        return clientBuilder
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient(DynamoDbProperties properties,
                                                            AwsCredentialsProvider awsCredentialsProvider) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient(properties, awsCredentialsProvider))
                .build();
    }

    @Bean
    public DynamoDbTemplate dynamoDbTemplate(DynamoDbProperties properties, AwsCredentialsProvider awsCredentialsProvider) {
        return new DynamoDbTemplate(getDynamoDbEnhancedClient(properties, awsCredentialsProvider));
    }

}
