package com.example.VoiceTasker.repository;

import com.example.VoiceTasker.model.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCalendarRepository extends JpaRepository<UserCalendar, Long> {
    Optional<UserCalendar> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
} 