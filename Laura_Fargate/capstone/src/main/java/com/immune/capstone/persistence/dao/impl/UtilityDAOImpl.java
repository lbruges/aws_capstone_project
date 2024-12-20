package com.immune.capstone.persistence.dao.impl;

import com.immune.capstone.config.properties.DynamoDbProperties;
import com.immune.capstone.mapper.UtilityMapper;
import com.immune.capstone.model.Utility;
import com.immune.capstone.persistence.entity.UtilityEntity;
import com.immune.capstone.persistence.dao.UtilityDAO;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.immune.capstone.persistence.utils.DynamoDbConstants.DYNAMO_DATE_SEC_INDEX;

@Log4j2
@Component
@RequiredArgsConstructor
public class UtilityDAOImpl implements UtilityDAO {
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbProperties properties;
    private final UtilityMapper mapper;

    @Override
    public void save(Utility utility) {
        DynamoDbTable<UtilityEntity> utilTable = enhancedClient.table(properties.getTableName(),
                TableSchema.fromBean(UtilityEntity.class));
        utilTable.putItem(mapper.toEntity(utility));
    }

    @Override
    public Map<String, Utility> getUtilsByZonePerDate(String targetDate) {
        Map<String, Utility> utilsByZone = new HashMap<>();

        DynamoDbIndex<UtilityEntity> dateSecIndex = enhancedClient.table(properties.getTableName(),
                TableSchema.fromBean(UtilityEntity.class))
                .index(DYNAMO_DATE_SEC_INDEX);

        AttributeValue dateAttr = AttributeValue.fromS(targetDate);

        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(dateAttr)
                        .build());

        SdkIterable<Page<UtilityEntity>> results = dateSecIndex.query(
                QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .build()
        );

        for (var page : results) {
            if (page.items() == null || page.items().size() == 0) {
                log.warn("No results found for target date {}", targetDate);
                continue;
            }

            page.items().forEach(entity -> addUtilToMap(entity, utilsByZone, targetDate));
        }

        return utilsByZone;
    }

    private void addUtilToMap(UtilityEntity entity, Map<String, Utility> utilsByZone, String targetDate) {
        String zoneId = entity.getZone();
        log.info("Fetching utilities for zone id {} and month {}", zoneId, targetDate);

        utilsByZone.put(zoneId, mapper.toModel(entity));
    }
}
