package com.backend.bankingapp.models.users;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserFactory {
    public static User createUser(UserDTO userDTO, Role role) throws Exception {
        switch (role.getName()){
            case "ROLE_ADMIN":
                Admin admin = new Admin(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role);
                return admin;
            case "ROLE_CLIENT":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate birthDate = LocalDate.parse(userDTO.getBirthDateString(), formatter);
                return new AccountHolder(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), role, birthDate, userDTO.getPrimaryAddress(), userDTO.getMailingAddress());
            default:
                throw new Exception("Error creating User.");
        }
    }
}
