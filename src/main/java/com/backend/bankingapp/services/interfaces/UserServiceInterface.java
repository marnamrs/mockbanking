package com.backend.bankingapp.services.interfaces;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.models.users.User;

import javax.management.relation.RoleNotFoundException;
import java.util.List;


public interface UserServiceInterface {

    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    User getUserById(Long id);
    List<User> getUsers();
    //Transform userDTO into User
    User createUser(UserDTO userDTO);
    User createClient(UserDTO userDTO);
}

