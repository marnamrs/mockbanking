package com.backend.bankingapp;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.services.impl.AdminService;
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
    CommandLineRunner run(AdminService adminService) {
        return args -> {
//            Add roles:
            adminService.saveRole(new Role(null, "ROLE_ADMIN"));
            adminService.saveRole(new Role(null, "ROLE_CLIENT"));
            adminService.saveRole(new Role(null, "ROLE_EXTERNAL"));
//            Add admin:
            adminService.createUser(new UserDTO("Admin", "admin", "1234","ROLE_ADMIN"));
//            Add accountHolder:
            adminService.createClient(new UserDTO("User", "user1", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
            adminService.createClient(new UserDTO("User", "user2", "1234", "ROLE_CLIENT", "01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, "Spain", "Madrid", "Street", 99, 90009));
//            Add thirdParty:
            adminService.createExternal("External1");
//            Add checkingAccount:


        };
    }

}
