//package com.example.VoiceTasker.service;
//
//import com.example.VoiceTasker.model.Task;
//import com.example.VoiceTasker.model.UserCalendar;
//import com.example.VoiceTasker.repository.UserCalendarRepository;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventDateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.security.GeneralSecurityException;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.List;
//
//@Service
//public class CalendarService {
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/calendar");
//
//    @Autowired
//    private UserCalendarRepository userCalendarRepository;
//
//    @Value("${google.client.id}")
//    private String clientId;
//
//    @Value("${google.client.secret}")
//    private String clientSecret;
//
//    @Value("${google.redirect.uri}")
//    private String redirectUri;
//
//    public String getAuthorizationUrl() throws IOException, GeneralSecurityException {
//        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
//            new InputStreamReader(getClass().getResourceAsStream("/client_secrets.json")));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//            httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
//            .setAccessType("offline")
//            .setApprovalPrompt("force")
//            .build();
//
//        return flow.newAuthorizationUrl()
//            .setRedirectUri(redirectUri)
//            .build();
//    }
//
//    public void handleCallback(String code, String state) throws IOException, GeneralSecurityException {
//        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
//            new InputStreamReader(getClass().getResourceAsStream("/client_secrets.json")));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//            httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
//            .setAccessType("offline")
//            .setApprovalPrompt("force")
//            .build();
//
//        // Exchange the authorization code for tokens
//        var tokenResponse = flow.newTokenRequest(code)
//            .setRedirectUri(redirectUri)
//            .execute();
//
//        // Extract user ID from state parameter
//        Long userId = Long.parseLong(state);
//
//        // Save or update user calendar configuration
//        UserCalendar userCalendar = userCalendarRepository.findByUserId(userId)
//            .orElse(new UserCalendar());
//
//        userCalendar.setUserId(userId);
//        userCalendar.setAccessToken(tokenResponse.getAccessToken());
//        userCalendar.setRefreshToken(tokenResponse.getRefreshToken());
//        userCalendar.setTokenExpiryTime(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
//        userCalendar.setActive(true);
//        userCalendar.setGoogleCalendarId("primary"); // Use primary calendar by default
//
//        userCalendarRepository.save(userCalendar);
//    }
//
//    public boolean isCalendarConfigured(Long userId) {
//        return userCalendarRepository.existsByUserId(userId);
//    }
//
//    public void disconnectCalendar(Long userId) {
//        userCalendarRepository.deleteByUserId(userId);
//    }
//
//    public void syncTaskWithCalendar(Task task, Long userId) {
//        UserCalendar userCalendar = userCalendarRepository.findByUserId(userId)
//            .orElseThrow(() -> new RuntimeException("User calendar not configured"));
//
//        if (!userCalendar.isActive()) {
//            throw new RuntimeException("User calendar is not active");
//        }
//
//        try {
//            Calendar calendar = getCalendarService(userCalendar);
//            Event event = createEventFromTask(task);
//            calendar.events().insert(userCalendar.getGoogleCalendarId(), event).execute();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to sync task with calendar", e);
//        }
//    }
//
//    private Calendar getCalendarService(UserCalendar userCalendar) throws GeneralSecurityException, IOException {
//        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//
//        GoogleCredential credential = new GoogleCredential.Builder()
//            .setTransport(httpTransport)
//            .setJsonFactory(JSON_FACTORY)
//            .setClientSecrets(clientId, clientSecret)
//            .build();
//
//        credential.setAccessToken(userCalendar.getAccessToken());
//        credential.setRefreshToken(userCalendar.getRefreshToken());
//
//        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
//            .setApplicationName("Voice Tasker")
//            .build();
//    }
//
//    private Event createEventFromTask(Task task) {
//        Event event = new Event();
//        event.setSummary(task.getTask());
//        event.setDescription("Task from Voice Tasker");
//
//        // Convert task datetime to Google Calendar format
//        LocalDateTime taskDateTime = LocalDateTime.parse(task.getDatetime());
//        DateTime startDateTime = new DateTime(
//            taskDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
//        );
//
//        EventDateTime start = new EventDateTime();
//        start.setDateTime(startDateTime);
//        event.setStart(start);
//
//        // Set end time (1 hour duration by default)
//        DateTime endDateTime = new DateTime(
//            taskDateTime.plusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
//        );
//        EventDateTime end = new EventDateTime();
//        end.setDateTime(endDateTime);
//        event.setEnd(end);
//
//        return event;
//    }
//}