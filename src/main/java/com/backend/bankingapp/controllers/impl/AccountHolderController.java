package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AccountHolderController {

    //access available to everyone (register as new Client)
//    @PostMapping("/client/new")
//    @ResponseStatus(HttpStatus.CREATED)
//    public User createClient(@RequestBody UserDTO userDTO) {
//        return adminService.createClient(userDTO);
//    }

}
