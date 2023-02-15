package com.backend.bankingapp.models.utils;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.*;
import com.backend.bankingapp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserFactory {
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
                    //is providing primary and secondary address
                    Address secondary = new Address(userDTO.getMailingCountry(), userDTO.getMailingCity(), userDTO.getMailingStreet(), userDTO.getMailingStreetNum(), userDTO.getMailingZipCode());
                    return new AccountHolder(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role, birthDate, primary, secondary);
                } else {
                    //provides only primary address
                    return new AccountHolder(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role, birthDate, primary);
                }
            }
            case "ROLE_EXTERNAL" -> {
                ThirdParty user = new ThirdParty(userDTO.getName(), role);
                return user;
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating User.");
        }
    }
}
