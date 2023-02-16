package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.repositories.UserRepository;
import com.backend.bankingapp.services.impl.AccountHolderService;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class AccountHolderController {
    @Autowired
    private AdminServiceInterface adminService;
    @Autowired
    private AccountHolderService accountHolderService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public User getUserInfo(Authentication user){
        //Authentication user --> username + role
        return accountHolderService.getUserInfo(user);
    }
    //GET
    //TODO add get global balance
    //TODO add get balance by account
    //TODO add get transfers by account
    //TODO add get list of accounts
    //TODO add get account

    //POST
    //TODO add make transfer from account

    @PostMapping("/client/new-client")
    @ResponseStatus(HttpStatus.CREATED)
    public User createClient(@RequestBody UserDTO userDTO) {
        return adminService.createClient(userDTO);
    }
}
