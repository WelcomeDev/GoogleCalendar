package com.thewhite.calendar.service.arguments;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Created on 5/4/2022
 *
 * @author Fedor Ishchenko
 */
@Value
@Builder
public class CreateEventArguments {

    String calendarId;

    String title;

    String description;

    LocalDateTime timeFrom;

    LocalDateTime timeTo;

}
