package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.RoleToUserDTO;
import com.backend.bankingapp.controllers.interfaces.RoleControllerInterface;
import com.backend.bankingapp.models.users.Role;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * RESTful API for Role management
 */
@RestController
@RequestMapping("/api/admin")
public class RoleController implements RoleControllerInterface {

    @Autowired
    private AdminServiceInterface userService;

    @PostMapping("/role/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveRole(@RequestBody Role role) {
        userService.saveRole(role);
    }

    @PostMapping("/role/addtouser")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRoleToUser(@RequestBody RoleToUserDTO roleToUserDTO) {
        userService.addRoleToUser(roleToUserDTO.getUsername(), roleToUserDTO.getRoleName());
    }
}
