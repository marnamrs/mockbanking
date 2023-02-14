package com.backend.bankingapp.models.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {
    public Admin(String name, String username, String password, Role role) {
    }


//specific methods:
    //create thirdparty user
    //create account of any type
}
