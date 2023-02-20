package com.backend.bankingapp;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collection;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User admin = new User("test-admin", "admin-password", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        User accountHolder = new User("test-client", "client-password", Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));

        return new InMemoryUserDetailsManager(Arrays.asList(
                admin, accountHolder
        ));
    }
}