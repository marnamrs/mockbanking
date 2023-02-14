package com.backend.bankingapp.dtos;

import com.backend.bankingapp.models.users.Address;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String password;
    //in UserService layer Role is fetched by roleName from roleRepository
    @NotNull
    @NotEmpty
    private String roleName;
    private String birthDateString;
    private Address primaryAddress;
    private Address mailingAddress;
}
