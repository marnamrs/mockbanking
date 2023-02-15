package com.backend.bankingapp.models.utils;

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;


public class HashCreator {
    //creation of key

    public static String createKey(){
        //key generator (string)
        StringKeyGenerator generator = KeyGenerators.string();
        //generate 8-byte key
        return generator.generateKey();
    }

}
