package com.backend.bankingapp.controllers.interfaces;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.User;

import java.util.List;


public interface AdminControllerInterface {
    List<User> getUsers();
    User getUserById(Long id);
    User createUser(UserDTO userDTO);


}
