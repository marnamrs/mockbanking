package com.backend.bankingapp.models.users;

import com.backend.bankingapp.models.utils.HashCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ThirdParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String accessKey = setAccessKey();
    @ManyToOne
    private Role role;

    public ThirdParty(String name, Role role){
        setName(name);
        setRole(role);
    }

    private String setAccessKey() {
        //generates 8-byte key to be kept by thirdParty for future access
        //encrypted key will be stored in DB
        return HashCreator.createKey();
    }

}
