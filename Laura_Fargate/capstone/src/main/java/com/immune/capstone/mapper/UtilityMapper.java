package com.immune.capstone.mapper;

import com.immune.capstone.model.Utility;
import com.immune.capstone.persistence.entity.UtilityEntity;

public interface UtilityMapper {

    UtilityEntity toEntity(Utility utility);

    Utility toModel(UtilityEntity entity);

}
