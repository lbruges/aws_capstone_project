package com.immune.capstone.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import static com.immune.capstone.persistence.utils.DynamoDbConstants.DYNAMO_DATE_SEC_INDEX;
import static com.immune.capstone.persistence.utils.DynamoDbConstants.DYNAMO_DATE_ZONE_SEC_INDEX;

@Setter
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
public class UtilityEntity {

    private String id; // composite - zone_date
    private String zone;
    private String date;
    private float pricePerM3;
    private float utility;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }


    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = { DYNAMO_DATE_SEC_INDEX })
    public String getDate() {
        return date;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = { DYNAMO_DATE_ZONE_SEC_INDEX })
    public String getZone() {
        return zone;
    }

    @DynamoDbAttribute("price_per_m3")
    public float getPricePerM3() {
        return pricePerM3;
    }

    @DynamoDbAttribute("utility")
    public float getUtility() {
        return utility;
    }

}
