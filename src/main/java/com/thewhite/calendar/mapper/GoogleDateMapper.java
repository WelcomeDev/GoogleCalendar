package com.thewhite.calendar.mapper;

import com.google.api.client.util.DateTime;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created on 5/4/2022
 *
 * @author Fedor Ishchenko
 */
@Component
public class GoogleDateMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public DateTime fromLocalDateTime(LocalDateTime value) {
        // todo: это можно попробовать взять из календаря
        return DateTime.parseRfc3339(value.format(DATE_TIME_FORMATTER));
    }

}
