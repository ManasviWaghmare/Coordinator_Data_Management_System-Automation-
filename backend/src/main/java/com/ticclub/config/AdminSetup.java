package com.ticclub.config;

import com.ticclub.model.Role;
import com.ticclub.model.User;
import com.ticclub.repository.RoleRepository;
import com.ticclub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSetup {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        return args -> {
            if (!userRepo.existsByUsername("admin")) {
                Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_ADMIN");
                    return roleRepo.save(r);
                });

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setEmail("admin@ticclub.com");
                admin.setFullName("System Admin");
                admin.setRole(adminRole);
                userRepo.save(admin);
                System.out.println("Admin user created successfully with password: admin123");
            } else {
                User admin = userRepo.findByUsername("admin").get();
                admin.setPassword(encoder.encode("admin123"));
                userRepo.save(admin);
                System.out.println("Admin user password reset to: admin123");
            }
        };
    }
}
