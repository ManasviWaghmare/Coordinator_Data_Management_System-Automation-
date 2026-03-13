package com.ticclub.service;

import com.ticclub.dto.StudentDTO;
import com.ticclub.model.Student;
import com.ticclub.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public StudentDTO createStudent(StudentDTO dto) {
        Student student = new Student();
        mapToEntity(dto, student);
        return mapToDTO(studentRepository.save(student));
    }

    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        return studentRepository.findById(id).map(student -> {
            mapToEntity(dto, student);
            return mapToDTO(studentRepository.save(student));
        }).orElse(null);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    private void mapToEntity(StudentDTO dto, Student student) {
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setRollNumber(dto.getRollNumber());
        student.setDepartment(dto.getDepartment());
        student.setRegistrationYear(dto.getRegistrationYear());
        student.setPhoneNumber(dto.getPhoneNumber());
    }

    private StudentDTO mapToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setUsername(student.getUser().getUsername());
        }
        dto.setEmail(student.getEmail() != null ? student.getEmail()
                : (student.getUser() != null ? student.getUser().getEmail() : null));
        dto.setFullName(student.getFullName() != null ? student.getFullName()
                : (student.getUser() != null ? student.getUser().getFullName() : null));
        dto.setRollNumber(student.getRollNumber());
        dto.setDepartment(student.getDepartment());
        dto.setRegistrationYear(student.getRegistrationYear());
        dto.setPhoneNumber(student.getPhoneNumber());
        return dto;
    }
}
