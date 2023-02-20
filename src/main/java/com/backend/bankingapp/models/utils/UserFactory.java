package com.backend.bankingapp.models.utils;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class UserFactory {
    public static User createUser(UserDTO userDTO, Role role) {
        switch (role.getName()) {
            case "ROLE_ADMIN" -> {
                return new Admin(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role);
            }
            case "ROLE_CLIENT" -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate birthDate = LocalDate.parse(userDTO.getBirthDateString(), formatter);
                Address primary = new Address(userDTO.getPrimaryCountry(), userDTO.getPrimaryCity(), userDTO.getPrimaryStreet(), userDTO.getPrimaryStreetNum(), userDTO.getPrimaryZipCode());
                if (userDTO.getMailingCountry() != null) {
                    //provides primary and secondary address
                    Address secondary = new Address(userDTO.getMailingCountry(), userDTO.getMailingCity(), userDTO.getMailingStreet(), userDTO.getMailingStreetNum(), userDTO.getMailingZipCode());
                    return new AccountHolder(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role, birthDate, primary, secondary);
                } else {
                    //provides only primary address
                    return new AccountHolder(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role, birthDate, primary);
                }
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating User.");
        }
    }

    public static ThirdParty createExternal(String name, Role role) {
        return new ThirdParty(name, role);
    }
}
