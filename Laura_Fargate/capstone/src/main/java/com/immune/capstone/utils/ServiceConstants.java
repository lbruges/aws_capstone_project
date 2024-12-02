package com.immune.capstone.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;

public class ServiceConstants {

    public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("MM-yyyy")
            .parseDefaulting(DAY_OF_MONTH, 1)
            .toFormatter();

}
