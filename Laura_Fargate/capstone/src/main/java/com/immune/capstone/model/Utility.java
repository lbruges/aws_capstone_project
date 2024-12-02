package com.immune.capstone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utility {

    private String id; // composite - zone_date
    private String zone;
    private String date;
    private float pricePerM3;
    private float utility;

    public Map<String, String> toMap() {
        return Map.of("Zone", zone, "Date", date, "Price per M3", String.valueOf(pricePerM3), "Utility",
                String.valueOf(utility));
    }

}
