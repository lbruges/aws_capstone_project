package com.immune.capstone.mapper.impl;

import com.immune.capstone.mapper.UtilityMapper;
import com.immune.capstone.model.Utility;
import com.immune.capstone.persistence.entity.UtilityEntity;
import org.springframework.stereotype.Component;

@Component
public class UtilityMapperImpl implements UtilityMapper {
    @Override
    public UtilityEntity toEntity(Utility utility) {
        return UtilityEntity.builder()
                .id(utility.getId())
                .date(utility.getDate())
                .pricePerM3(utility.getPricePerM3())
                .utility(utility.getUtility())
                .build();
    }

    @Override
    public Utility toModel(UtilityEntity entity) {
        return Utility.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .pricePerM3(entity.getPricePerM3())
                .utility(entity.getUtility())
                .build();
    }
}
