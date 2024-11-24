package com.immune.capstone.persistence.repository.impl;

import com.immune.capstone.config.properties.DynamoDbProperties;
import com.immune.capstone.persistence.entity.UtilityEntity;
import com.immune.capstone.persistence.repository.UtilityRepository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.immune.capstone.persistence.utils.DynamoDbConstants.DYNAMO_DATE_SEC_INDEX;

@Log4j2
@Repository
@RequiredArgsConstructor
public class UtilityRepositoryImpl implements UtilityRepository {

    private final DynamoDbTemplate dbTemplate;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbProperties properties;

    @Override
    public UtilityEntity save(UtilityEntity entity) {
        return dbTemplate.save(entity);
    }

    @Override
    public UtilityEntity update(UtilityEntity entity) {
        return dbTemplate.update(entity);
    }

    @Override
    public Map<String, UtilityEntity> getUtilsByZonePerDate(String targetDate) {
        Map<String, UtilityEntity> utilsByZone = new HashMap<>();

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

        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.set(0);
        results.forEach(page -> {
            UtilityEntity entity = page.items().get(atomicInteger.get());
            String zoneId = entity.getZone();
            log.info("Fetching utilities for zone id {} and month {}", zoneId, targetDate);

            utilsByZone.put(zoneId, entity);
            atomicInteger.incrementAndGet();
        });

        return utilsByZone;
    }
}
