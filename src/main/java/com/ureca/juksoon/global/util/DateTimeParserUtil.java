package com.ureca.juksoon.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeParserUtil {

    public static LocalDateTime toLocalDateTime(String localDateString){
        LocalDate localDate = LocalDate.parse(localDateString);
        LocalTime localTime = LocalTime.of(0, 0, 0, 0);
        return LocalDateTime.of(localDate, localTime);
    }
}
