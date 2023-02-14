package com.backend.bankingapp.services.interfaces;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.models.users.User;

import javax.management.relation.RoleNotFoundException;
import java.util.List;


public interface UserServiceInterface {

    User saveUser(User user);
    Role saveRole(Role role);

    /**
     * This method is used to add a Role to a User.
     *
     * @param username the username of the User to which the Role is to be added.
     * @param roleName the name of the Role to be added.
     */
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
    //Transform userDTO into User
    User createUser(UserDTO userDTO) throws Exception;
}

