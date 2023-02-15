package com.backend.bankingapp;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.Admin;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.services.impl.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class bankingApplication {

    public static void main(String[] args) {

        SpringApplication.run(bankingApplication.class, args);
        System.out.println("App running");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
//            Add roles:
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_CLIENT"));
            userService.saveRole(new Role(null, "ROLE_EXTERNAL"));
//            Add admin:
            userService.createUser(new UserDTO("Admin", "admin", "1234","ROLE_ADMIN"));
//            Add accountHolder:
            userService.createUser(new UserDTO("User", "user", "1234", "ROLE_CLIENT", "01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
            userService.createUser(new UserDTO("User", "user", "1234", "ROLE_CLIENT", "01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, "Spain", "Madrid", "Street", 99, 90009));

        };
    }

}
