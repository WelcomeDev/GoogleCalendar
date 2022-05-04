package com.thewhite.calendar.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.thewhite.calendar.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 5/4/2022
 *
 * @author Fedor Ishchenko
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

    private final AuthService authService;

    @Bean
    public Calendar getCalendar() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        authService.authenticate(httpTransport);
        return new Calendar.Builder(httpTransport, JSON_FACTORY, authService.getCredential())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
