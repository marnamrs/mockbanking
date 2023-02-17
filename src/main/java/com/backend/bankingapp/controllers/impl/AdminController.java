package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.controllers.interfaces.AdminControllerInterface;
import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController implements AdminControllerInterface {

    @Autowired
    private AdminServiceInterface adminService;


    //GET
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return adminService.getUsers();
    }
    @GetMapping("/users/id")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@RequestParam Long id){
        return adminService.getUserById(id);
    }

    @GetMapping("/externals")
    @ResponseStatus(HttpStatus.OK)
    public List<ThirdParty> getExternals() { return adminService.getExternals(); }
    @GetMapping("/externals/id")
    @ResponseStatus(HttpStatus.OK)
    public ThirdParty getExternalById(@RequestParam Long id){ return adminService.getExternalById(id); }

    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAccounts(){return adminService.getAccounts();};
    @GetMapping("/accounts/id")
    @ResponseStatus(HttpStatus.OK)
    public Account getAccountById(@RequestParam Long id){return adminService.getAccountById(id);};

    //POST
    @PostMapping("/users/add")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserDTO userDTO)  {
        return adminService.createUser(userDTO);
    }
    @PostMapping("/externals/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty createExternal(@RequestBody String name)  {
        return adminService.createExternal(name);
    }

    @PostMapping("/accounts/add/checking")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createCheckingAccount(@RequestBody AccountDTO accountDTO) {
        return null;
    }
    @PostMapping("/accounts/add/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createSavingsAccount() {
        return null;
    }
    @PostMapping("/accounts/add/credit")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createCreditCard() {
        return null;
    }
    @PostMapping("/accounts/update/balance")
    @ResponseStatus(HttpStatus.OK)
    public Account setAccountBalance(@RequestParam Long accountId, @RequestParam double amount) {
        return adminService.setBalance(accountId, amount);
    }
//TODO add admin POST add client to account
    //TODO add admin GET accounts
    //TODO add admin GET balance by client || account



}
