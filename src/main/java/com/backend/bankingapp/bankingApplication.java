package com.backend.bankingapp;

import com.backend.bankingapp.models.Role;
import com.backend.bankingapp.models.User;
import com.backend.bankingapp.services.impl.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

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
//            Add new roles and users:
//            userService.saveRole(new Role(null, "ROLE_foo"));
//            userService.saveUser(new User(null, "name", "username", "password", new ArrayList<>()));
//            userService.addRoleToUser("username", "ROLE_foo");
        };
    }

}
