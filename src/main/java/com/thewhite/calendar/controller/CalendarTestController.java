package com.thewhite.calendar.controller;

import com.google.api.services.calendar.model.*;
import com.thewhite.calendar.service.AuthService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.thewhite.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created on 4/15/2022
 *
 * @author Fedor Ishchenko
 */
@Component
@RestController
@RequiredArgsConstructor
@Slf4j
public class CalendarTestController {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

    private final AuthService authService;

    private final CalendarService calendarService;

    @GetMapping
    public Credential auth() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return authService.authenticate(HTTP_TRANSPORT);
    }

    @GetMapping("free-busy")
    public Set<TimePeriod> getFreeBusy() throws Exception {
        return calendarService.getFreeBusy();
    }

    @GetMapping("events")
    public List<Event> getEvents() throws Exception {
        return calendarService.getEvents();
    }

}
