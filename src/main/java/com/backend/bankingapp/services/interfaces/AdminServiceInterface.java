package com.backend.bankingapp.services.interfaces;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.users.User;

import java.util.List;


public interface AdminServiceInterface {

    //Post: Users
    User createUser(UserDTO userDTO);
    User createClient(UserDTO userDTO);
    User saveUser(User user);
    User saveUser(ThirdParty user);

    //Post: Roles
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);

    //Get: Users
    User getUser(String username);
    User getUserById(Long id);
    List<User> getUsers();

    //TODO add profile services (Admin, AccountHolder, ThirdParty)
}

