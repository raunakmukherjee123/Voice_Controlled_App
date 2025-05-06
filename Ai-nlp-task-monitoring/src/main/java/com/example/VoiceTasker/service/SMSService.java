//package com.example.VoiceTasker.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SMSService {
//    @Value("${twilio.account.sid}")
//    private String ACCOUNT_SID;
//
//    @Value("${twilio.auth.token}")
//    private String AUTH_TOKEN;
//
//    @Value("${twilio.phone.number}")
//    private String FROM_NUMBER;
//
//    public void sendSMS(String to, String message) {
//        try {
//            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//            Message.creator(
//                new PhoneNumber(to),
//                new PhoneNumber(FROM_NUMBER),
//                message
//            ).create();
//        } catch (Exception e) {
//            System.err.println("Failed to send SMS: " + e.getMessage());
//        }
//    }
//}