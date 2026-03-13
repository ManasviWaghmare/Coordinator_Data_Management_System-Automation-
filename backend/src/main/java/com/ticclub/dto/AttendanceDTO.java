package com.ticclub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private Long eventId;
    private String eventTitle;
    private boolean isPresent;
    private LocalDateTime recordedAt;
}
