package com.immune.capstone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utility {

    private String id; // composite - zone_date
    private String zone;
    private String date;
    private double pricePerM3;
    private double utility;

}
