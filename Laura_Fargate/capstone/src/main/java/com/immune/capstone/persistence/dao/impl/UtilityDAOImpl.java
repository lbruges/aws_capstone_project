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
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.immune.capstone.persistence.utils.DynamoDbConstants.DYNAMO_DATE_SEC_INDEX;

@Log4j2
@Component
@RequiredArgsConstructor
public class UtilityDAOImpl implements UtilityDAO {

    private final DynamoDbTemplate dbTemplate;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbProperties properties;
    private final UtilityMapper mapper;

    @Override
    public Utility save(Utility utility) {
        UtilityEntity entity = dbTemplate.save(mapper.toEntity(utility));
        return mapper.toModel(entity);
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

        if (!results.iterator().hasNext()) {
            return Collections.emptyMap();
        }

        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.set(0);

        results.forEach(page -> {
            UtilityEntity entity = page.items().get(atomicInteger.get());
            String zoneId = entity.getZone();
            log.info("Fetching utilities for zone id {} and month {}", zoneId, targetDate);

            utilsByZone.put(zoneId, mapper.toModel(entity));
            atomicInteger.incrementAndGet();
        });

        return utilsByZone;
    }
}
