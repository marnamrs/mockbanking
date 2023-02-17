package com.backend.bankingapp.models.users;

import jakarta.persistence.Entity;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {
    public Admin(String name, String username, String password, Role role) {
    super(name, username, password, role);
    }

}
