package com.ecommerce.config;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {
    
    @Bean
    CommandLineRunner createDefaultAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = "admin@ecommerce.com";
            String adminpassword = "admin123";

            // check if admin already exist
            boolean adminExist =
                        userRepository
                                .findByEmail(adminEmail)
                                .isPresent();
            if(!adminExist) {

                User admin = new User();

                admin.setName("E-Commerce Admin");
                admin.setEmail(adminEmail);

                // Encrypt password
                admin.setPassword(
                    passwordEncoder.encode(
                        adminpassword
                    )
                );

                admin.setRole("ADMIN");

                userRepository.save(admin);

                System.out.println(
                    "============================="
                );

                System.out.println(
                    "DEFAULT ADMIN CREATED"
                );

                System.out.println(
                    "Email: "
                            + adminEmail
                );

                System.out.println(
                    "Password: "
                                + adminpassword
                );

                System.out.println(
                    "==============================="
                );
            } else {
                System.out.println(
                    "Admin is already exist"
                );
            }
        };
    }
}
