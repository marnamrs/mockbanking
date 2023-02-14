package com.backend.bankingapp.dtos;

import com.backend.bankingapp.models.utils.Address;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
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
    //TODO see how to handle Address in UserDTO
    private Address primaryAddress;
    private Address mailingAddress;

}
