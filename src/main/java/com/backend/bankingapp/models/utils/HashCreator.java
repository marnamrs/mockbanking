package com.backend.bankingapp.models.utils;

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;


public class HashCreator {
    private static long accountNumber;
    private static int accountNumLength = 8;

    //creation of key
    public static String createKey(){
        //key generator (string)
        StringKeyGenerator generator = KeyGenerators.string();
        //generate 8-byte key
        return generator.generateKey();
    }

    public static String createAccountKey(){
        //increase number
        long nextNum = accountNumber + 1;
        //save new number
        accountNumber = nextNum;
        //convert to string and make it 8char in length
        String key = String.valueOf(nextNum);
        while(key.length()<accountNumLength){
            key = "0".concat(key);
        }
        return key;
    }

}
