package com.example.VoiceTasker.service;

import com.example.VoiceTasker.model.Task;
import com.example.VoiceTasker.Dao.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(String operation, String task, String urgency, String datetime) {
        Task newTask = new Task();
        newTask.setOperation(operation);
        newTask.setTask(task);
        newTask.setUrgency(urgency);
        newTask.setDatetime(datetime);
        return taskRepository.save(newTask);
    }

    public Task updateTask(Long id, String operation, String task, String urgency, String datetime) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task existingTask = taskOptional.get();
            existingTask.setOperation(operation);
            existingTask.setTask(task);
            existingTask.setUrgency(urgency);
            existingTask.setDatetime(datetime);
            return taskRepository.save(existingTask);
        }
        return null;
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Task> getTask(Long id) {
        return taskRepository.findById(id);
    }

    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
