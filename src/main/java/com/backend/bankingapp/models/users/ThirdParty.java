package com.backend.bankingapp.models.users;

import com.backend.bankingapp.models.utils.HashCreator;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ThirdParty extends User {
    private String userkey;

    public ThirdParty(String name, Role role){
        super(name, role);
        setKey();
    }

    public void setKey() {
        //generates 8-byte key to be stored by User for future access
        //encrypted key will be stored in DB
        this.userkey = HashCreator.createKey();
    }

}
