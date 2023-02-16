package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST API for User management

@RestController
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private AdminServiceInterface adminService;


    @GetMapping("/adm/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/adm/users/id")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@RequestParam Long id){
        return adminService.getUserById(id);
    }

    @PostMapping("/adm/users/add")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserDTO userDTO)  {
        return adminService.createUser(userDTO);
    }

    //TODO add admin POST new accounts
    //TODO add admin POST add client to account
    //TODO add admin GET accounts
    //TODO add admin GET balance by client || account
    //TODO add admin POST balance to account


}
