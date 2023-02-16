package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.repositories.ThirdPartyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class ExternalService {
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    //TODO add accountrepository
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Boolean verifyKey(String key){
        boolean isValid = false;
        for(ThirdParty u : thirdPartyRepository.findAll()){
            if(passwordEncoder.matches(key, u.getAccessKey())){
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    public ThirdParty getExternalByName(String name, String key){
        log.info("Fetching external party {}", name);
        //check if key is valid
        if(verifyKey(key)){
            //get all external parties with given name
            List<ThirdParty> nameMatches = thirdPartyRepository.findByName(name);
            //check if there is a match for key and name. If so, return match.
            for(ThirdParty u : nameMatches){
                if(passwordEncoder.matches(key, u.getAccessKey())){
                    return u;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied.");
    }

    //TODO complete transaction method for Ext
    public String makeTransaction(String key, String amount, Long accountId, String accountKey ) {
        String message = null;
        if(verifyKey(key)){
            // find account by accountkey
            // increase or decrease by amount
        } else {
            message = "Invalid access key.";
        };
        return message;
    }


}
