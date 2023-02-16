package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.repositories.RoleRepository;
import com.backend.bankingapp.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class AccountHolderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public User getUserInfo(Authentication user) {
        log.info("Fetching user {}", user.getPrincipal());
        System.out.println("-----user: " + user);
        System.out.println("-----user.principal: " + user.getPrincipal());
        return userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
    }


}
