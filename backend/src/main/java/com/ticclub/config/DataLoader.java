package com.ticclub.config;

import com.ticclub.model.*;
import com.ticclub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        Role adminRole = seedRole("ROLE_ADMIN");
        seedRole("ROLE_COORDINATOR");

        // 2. Seed Admin User
        User admin = null;
        if (!userRepository.existsByUsername("admin")) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@ticclub.com");
            admin.setFullName("System Administrator");
            admin.setRole(adminRole);
            admin.setEnabled(true);
            admin = userRepository.save(admin);
            System.out.println("Admin user seeded: admin / admin123");
        } else {
            admin = userRepository.findByUsername("admin").get();
        }

        // 3. Seed Students
        if (studentRepository.count() == 0) {
            seedStudents();
        }

        // 4. Seed Events
        if (eventRepository.count() == 0) {
            seedEvents(admin);
        }

        // 5. Seed Teams
        if (teamRepository.count() == 0) {
            seedTeams(admin);
        }

        // 6. Seed Attendance (if students and events exist)
        if (attendanceRepository.count() == 0) {
            seedAttendance();
        }
    }

    private Role seedRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(new Role(name)));
    }

    private void seedStudents() {
        List<String> depts = Arrays.asList(
                "Artificial Intelligence & Data Science",
                "Computer Engineering",
                "Information Technology",
                "Computer Science (Data Science)");

        for (int i = 1; i <= 10; i++) {
            Student s = new Student();
            s.setFullName("Student " + i);
            s.setEmail("student" + i + "@example.com");
            s.setRollNumber("TIC" + (2024000 + i));
            s.setDepartment(depts.get(i % depts.size()));
            s.setRegistrationYear(2024);
            s.setPhoneNumber("+91 987654321" + (i % 10));
            studentRepository.save(s);
        }
    }

    private void seedEvents(User creator) {
        String[] titles = { "AI Workshop", "Web Stack Hackathon", "Cloud Computing Seminar", "Data Structures Quiz" };
        for (int i = 0; i < titles.length; i++) {
            Event e = new Event();
            e.setTitle(titles[i]);
            e.setDescription("Description for " + titles[i]);
            e.setEventDate(LocalDateTime.now().plusDays(i + 1));
            e.setLocation(i % 2 == 0 ? "Main Hall" : "Lab 402");
            e.setCreatedBy(creator);
            eventRepository.save(e);
        }
    }

    private void seedTeams(User creator) {
        String[] names = { "Neural Knights", "Cyber Wizards", "Data Dynamos" };
        List<Student> students = studentRepository.findAll();

        for (int i = 0; i < names.length; i++) {
            Team t = new Team();
            t.setName(names[i]);
            t.setDescription("Team working on " + names[i] + " projects.");
            t.setCreatedBy(creator);
            if (students.size() > 3) {
                t.getMembers().add(students.get(i));
                t.getMembers().add(students.get(i + 1));
            }
            teamRepository.save(t);
        }
    }

    private void seedAttendance() {
        List<Event> events = eventRepository.findAll();
        List<Student> students = studentRepository.findAll();

        if (!events.isEmpty() && !students.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                Attendance a = new Attendance();
                a.setEvent(events.get(0));
                a.setStudent(students.get(i));
                a.setPresent(i % 2 == 0);
                a.setRecordedAt(LocalDateTime.now());
                attendanceRepository.save(a);
            }
        }
    }
}
