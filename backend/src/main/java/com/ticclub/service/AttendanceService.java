package com.ticclub.service;

import com.ticclub.dto.AttendanceDTO;
import com.ticclub.model.Attendance;
import com.ticclub.model.Event;
import com.ticclub.model.Student;
import com.ticclub.repository.AttendanceRepository;
import com.ticclub.repository.EventRepository;
import com.ticclub.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<AttendanceDTO> getAttendanceByEvent(Long eventId) {
        return attendanceRepository.findByEventId(eventId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AttendanceDTO markAttendance(AttendanceDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId()).orElse(null);
        Event event = eventRepository.findById(dto.getEventId()).orElse(null);

        if (student == null || event == null)
            return null;

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setEvent(event);
        attendance.setPresent(dto.isPresent());
        attendance.setRecordedAt(LocalDateTime.now());

        return mapToDTO(attendanceRepository.save(attendance));
    }

    public List<AttendanceDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO mapToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setStudentName(attendance.getStudent().getFullName());
        dto.setStudentRollNumber(attendance.getStudent().getRollNumber());
        dto.setEventId(attendance.getEvent().getId());
        dto.setEventTitle(attendance.getEvent().getTitle());
        dto.setPresent(attendance.isPresent());
        dto.setRecordedAt(attendance.getRecordedAt());
        return dto;
    }
}
