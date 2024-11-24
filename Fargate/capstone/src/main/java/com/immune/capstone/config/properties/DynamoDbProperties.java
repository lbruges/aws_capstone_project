package com.immune.capstone.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "aws.dynamodb")
public class DynamoDbProperties {

    private String accessKey;
    private String secretKey;
    private String endpointOverride;
    private String region;
    private String tableName;

}
