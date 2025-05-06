package com.example.VoiceTasker.service;

import com.example.VoiceTasker.model.Task;
import com.example.VoiceTasker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;
    
//    @Autowired
//    private SMSService smsService;

    public void sendTaskNotifications(Task task, User user, boolean isUpdate) {
        if (user != null) {
            String action = isUpdate ? "updated" : "created";
            String emailMessage = String.format("Task %s: %s\nUrgency: %s\nDue: %s", 
                action, task.getTask(), task.getUrgency(), task.getDatetime());
            
            emailService.sendTaskNotification(
                user.getEmail(),
                "Task " + action.substring(0, 1).toUpperCase() + action.substring(1),
                emailMessage
            );
            
//            // Send SMS if phone number is available
//            if (user.getPhoneNumber() != null) {
//                smsService.sendSMS(
//                    user.getPhoneNumber(),
//                    "Task " + action + ": " + task.getTask() + " (Urgency: " + task.getUrgency() + ")"
//                );
//            }
        }
    }
} 