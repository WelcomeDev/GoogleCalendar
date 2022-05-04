package com.thewhite.calendar.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final Calendar calendarService;

    @SneakyThrows
    public List<Event> getEvents() {

        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = calendarService.events().list("primary")
                                       .setMaxResults(10)
                                       .setTimeMin(now)
                                       .setOrderBy("startTime")
                                       .setSingleEvents(true)
                                       .execute();
        return events.getItems();
    }

    @SneakyThrows
    public Set<TimePeriod> getFreeBusy() {
        DateTime now = new DateTime(System.currentTimeMillis());

        FreeBusyRequest freeBusyRequest = new FreeBusyRequest();
        freeBusyRequest.setTimeMin(now);
        CalendarList calendarList = calendarService.calendarList().list().execute();

        // обязательно указать календарь
        freeBusyRequest.setItems(calendarList.getItems().stream()
                                             .map(calendar -> new FreeBusyRequestItem().setId(calendar.getId()))
                                             .collect(Collectors.toList()));

        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        LocalDateTime endOfToday = LocalDateTime.of(nowLocalDateTime.getYear(), nowLocalDateTime.getMonth(), nowLocalDateTime.getDayOfMonth() + 21, 23, 59, 59);
        Date from = Date.from(endOfToday.toInstant(OffsetDateTime.now().getOffset()));
        // перенос на следующий день
        freeBusyRequest.setTimeMax(new DateTime(from));

        FreeBusyResponse freeBusyResponse = calendarService.freebusy().query(freeBusyRequest).execute();

        if (freeBusyResponse.getGroups() == null) log.info("No groups found");
        else freeBusyResponse.getGroups().forEach(((s, freeBusyGroup) -> log.info("FOUND groups {}", s)));

        Set<TimePeriod> timePeriods = new HashSet<>();
        if (freeBusyResponse.getCalendars() == null) log.info("No calendars found");
        else {
            freeBusyResponse.getCalendars().forEach((s, freeBusyCalendar) -> log.info("FOUND calendar {}", s));
            freeBusyResponse.getCalendars().forEach((s, calendar) -> {
                timePeriods.addAll(calendar.getBusy());
                if (s.equals("aleks180700@gmail.com")) log.info("Found {} my events", calendar.getBusy().size());
            });
        }

        return timePeriods;
    }

}
