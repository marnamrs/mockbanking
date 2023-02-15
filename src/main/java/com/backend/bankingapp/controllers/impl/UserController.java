package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST API for User management

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserServiceInterface userService;
    //TODO set access restrictions to userController methods

    //access available to Admin
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }
    //access available to Admin
    @GetMapping("/users/id")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@RequestParam Long id){
        return userService.getUserById(id);
    }

    //access available to Admin
    @PostMapping("/users/add")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserDTO userDTO)  {
        return userService.createUser(userDTO);
    }

    //access available to everyone (register as new Client)
    @PostMapping("/client/new")
    @ResponseStatus(HttpStatus.CREATED)
    public User createClient(@RequestBody UserDTO userDTO) {
        return userService.createClient(userDTO);
    }
}
