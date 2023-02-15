package com.backend.bankingapp.dtos;

import com.backend.bankingapp.models.utils.Address;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull
    @NotEmpty
    private String birthDateString;
    //primary address for Account Holder
    @NotNull
    @NotEmpty
    private String primaryCountry;
    @NotNull
    @NotEmpty
    private String primaryCity;
    private String primaryStreet;
    @Positive
    private int primaryStreetNum;
    @Positive
    private int primaryZipCode;
    //optional secondary address for AccountHolder
    private String mailingCountry;
    private String mailingCity;
    private String mailingStreet;
    @Positive
    private int mailingStreetNum;
    @Positive
    private int mailingZipCode;

    //Admin & AccountHolder profiles
    public UserDTO(String name, String username, String password, String roleName) {
        setName(name);
        setUsername(username);
        setPassword(password);
        setRoleName(roleName);
    }

    //thirdParty profiles
    public UserDTO(String name, String roleName){
        setName(name);
        setRoleName(roleName);
    }
}
