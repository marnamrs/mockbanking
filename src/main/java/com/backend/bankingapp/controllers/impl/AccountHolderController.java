package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.repositories.usersrepos.UserRepository;
import com.backend.bankingapp.services.impl.AccountHolderService;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/client")
public class AccountHolderController {
    @Autowired
    private AdminServiceInterface adminService;
    @Autowired
    private AccountHolderService accountHolderService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public User getUserInfo(Authentication user){
        //Authentication user --> username + role
        return accountHolderService.getUserInfo(user);
    }
    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getUserAccounts(Authentication user){
        return accountHolderService.getAccounts(user);
    }
    @GetMapping("/accounts/id")
    @ResponseStatus(HttpStatus.OK)
    public Account getUserAccount(Authentication user, @RequestParam Long accountId){
        return accountHolderService.getAccountById(user, accountId);
    }
    @GetMapping("/accounts/balance")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getGlobalBalance(Authentication user){
        return accountHolderService.getBalance(user);
    }

    //POST
    //TODO add make transfer from account

    @PostMapping("/client/new-client")
    @ResponseStatus(HttpStatus.CREATED)
    public User createClient(@RequestBody UserDTO userDTO) {
        return adminService.createClient(userDTO);
    }
}
