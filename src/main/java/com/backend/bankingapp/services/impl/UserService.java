package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.*;
import com.backend.bankingapp.models.utils.UserFactory;
import com.backend.bankingapp.repositories.RoleRepository;
import com.backend.bankingapp.repositories.UserRepository;
import com.backend.bankingapp.services.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findByUsername(username).isEmpty()) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            User user = userRepository.findByUsername(username).get();
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    @Override
    public User createUser(UserDTO userDTO) {
        log.info("Creating new user {} with role {}", userDTO.getName(), userDTO.getRoleName());
        User user = null;        
        Role role = roleRepository.findByName(userDTO.getRoleName());
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found in the database");
        }
        user = UserFactory.createUser(userDTO, role);
        return saveUser(user);
    }

    public User createClient(UserDTO userDTO) {
        log.info("Creating new user {} with role Client", userDTO.getName());
        User user = UserFactory.createUser(userDTO, roleRepository.findByName("ROLE_CLIENT"));
        return saveUser(user);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepository.findByUsername(username).get();
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }
    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        if(userRepository.findByUsername(username).isPresent()){
            return userRepository.findByUsername(username).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public User getUserById(Long id) {
        log.info("Fetching user {}", id);
        if(userRepository.findUserById(id).isPresent()){
            return userRepository.findUserById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }
}
