package com.example.VoiceTasker.controller;

import com.example.VoiceTasker.model.Task;
import com.example.VoiceTasker.model.User;
import com.example.VoiceTasker.repository.UserRepository;
import com.example.VoiceTasker.service.NotificationService;
import com.example.VoiceTasker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task.getOperation(), task.getTask(), task.getUrgency(), task.getDatetime());
        
        if (createdTask != null && task.getUserId() != null) {
            Optional<User> user = userRepository.findById(task.getUserId());
            user.ifPresent(userData -> notificationService.sendTaskNotifications(task, userData, false));
        }
        
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        Optional<Task> task = taskService.getTask(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Task>> getAllTasks() {
        Iterable<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task.getOperation(), task.getTask(), task.getUrgency(), task.getDatetime().toString());
        
        if (updatedTask != null) {
            if (task.getUserId() != null) {
                Optional<User> user = userRepository.findById(task.getUserId());
                user.ifPresent(userData -> notificationService.sendTaskNotifications(task, userData, true));
            }
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
