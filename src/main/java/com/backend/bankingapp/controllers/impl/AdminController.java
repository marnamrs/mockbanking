package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

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


    //TODO add admin POST new accounts
    //TODO add admin POST add client to account
    //TODO add admin GET accounts
    //TODO add admin GET balance by client || account
    //TODO add admin POST balance to account


}
