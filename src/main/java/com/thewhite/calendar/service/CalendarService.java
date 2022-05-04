package com.thewhite.calendar.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.thewhite.calendar.mapper.GoogleDateMapper;
import com.thewhite.calendar.service.arguments.CreateEventArguments;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final Calendar calendarService;

    private final GoogleDateMapper dateMapper;

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

        calendarList.getItems().forEach(calendarListEntry -> log.info("Calendar id: {}", calendarListEntry.getId()));

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

    public Event create(CreateEventArguments arguments) throws IOException {
//        EventReminder[] reminderOverrides = new EventReminder[]{
//                new EventReminder().setMethod("email").setMinutes(2 * 60),
//                new EventReminder().setMethod("popup").setMinutes(10),
//        };

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee()
                        .setEmail("fedor.ishchenko.18@gmail.com")
                        .setComment("Myself duplication")
                        .set("telegramId", "https://t.me/FedorIshchenko18"),
        };

        // todo: fix error on create
        Event event = new Event()
                .setSummary(arguments.getTitle())
                .setLocation("KHV, Komsomolskaya street 73")
                .setDescription(arguments.getDescription())
                .setStart(new EventDateTime().setDate(dateMapper.fromLocalDateTime(arguments.getTimeFrom()))
                                             .setTimeZone("Russia/Vladivostok"))
                .setEnd(new EventDateTime().setDate(dateMapper.fromLocalDateTime(arguments.getTimeTo()))
                                           .setTimeZone("Russia/Vladivostok"))
//                .setReminders(new Event.Reminders().setUseDefault(false)
//                                                   .setOverrides(Arrays.asList(reminderOverrides)))
                .setAttendees(Arrays.asList(attendees))
                .set("Joyful", true);

        return calendarService.events()
                              .insert(arguments.getCalendarId(), event)
                              .execute();
    }

}
