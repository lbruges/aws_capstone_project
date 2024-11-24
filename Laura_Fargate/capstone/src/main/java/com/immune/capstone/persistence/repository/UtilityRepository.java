package com.immune.capstone.persistence.repository;

import com.immune.capstone.persistence.entity.UtilityEntity;

import java.util.Map;

public interface UtilityRepository {

    UtilityEntity save(UtilityEntity entity);
    UtilityEntity update(UtilityEntity entity);
    Map<String, UtilityEntity> getUtilsByZonePerDate(String targetDate);

}
