package com.thewhite.calendar.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * Created on 4/15/2022
 *
 * @author Fedor Ishchenko
 */
@Service
public class AuthService {

    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Просто CalendarScopes.CALENDAR_EVENTS не подойдет, нужен доступ выше. Читать весь календарь
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Getter
    private Credential credential;

    @Value("${server.auth.port}")
    private Integer serverPort;

    @Value("classpath:" + CREDENTIALS_FILE_PATH)
    Resource resourceFile;

    public Credential authenticate(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream secretsInputStream = resourceFile.getInputStream();

        if (secretsInputStream == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(secretsInputStream));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(serverPort).build();
        credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

}
