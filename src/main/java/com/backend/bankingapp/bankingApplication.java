package com.backend.bankingapp;

import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.services.impl.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

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
            /*
             * Run adminService methods (initial Admin User creation, etc.)
             */

            /*
            * Generation of Basic Roles/Auth.
            * Changing these roles will require updating securityConfig file
            * */
            adminService.saveRole(new Role(null, "ROLE_ADMIN"));
            adminService.saveRole(new Role(null, "ROLE_CLIENT"));
            adminService.saveRole(new Role(null, "ROLE_EXTERNAL"));
            /*
             * To generate initial admin:
             * adminService.createUser(new UserDTO({name}, {username}, {password},"ROLE_ADMIN"));
             * */

        };
    }

}
