package com.backend.bankingapp.models.users;

import com.backend.bankingapp.models.utils.HashCreator;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ThirdParty extends User {
    private String key;

    public ThirdParty(String name, Role role){
        super(name, role);
        //TODO check way to handle exception outside constructor
        try {
            setKey(name);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public void setKey(String input) throws NoSuchAlgorithmException {
        String hash = null;
        try {
            hash = HashCreator.createSHAHash(input);
        } catch(Exception e) {
            System.err.println(e);
        }
        key = hash;
    }

}
