package com.backend.bankingapp.models.utils;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class HashCreator {
    //creation of SHA hashes

    //TODO test key creation
    public static String createSHAHash(String input) throws NoSuchAlgorithmException {
        String hash = null;
        //SHA function
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        //concat timestamp to avoid equal keys for thirdParties with equal name
        input = input.concat(String.valueOf(new Timestamp(System.currentTimeMillis())));
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        hash = convertToHex(digest);
        return hash;
    }

    private static String convertToHex(byte[] input){
        BigInteger bigint = new BigInteger(1, input);
        //string representation of the bigint
        String hexText = bigint.toString(16);
        //ensures constant length (32bytes) adding zeros
        while (hexText.length()<32){
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
