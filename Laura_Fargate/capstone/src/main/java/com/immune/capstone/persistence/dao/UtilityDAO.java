package com.immune.capstone.persistence.dao;

import com.immune.capstone.model.Utility;

import java.util.Map;

public interface UtilityDAO {

    void save(Utility utility);
    Map<String, Utility> getUtilsByZonePerDate(String targetDate);

}
