package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.repositories.accountrepos.AccountRepository;
import com.backend.bankingapp.repositories.usersrepos.ThirdPartyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class ExternalService {
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
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

    //TODO review makeTransaction() for ThirdParty calling AccService
    public String makeTransaction(String userKey, TransactionDTO transactionDTO) {
        log.info("Verifying external party access key");
        if(verifyKey(userKey)){
            log.info("Successful access key verification");
            accountService.createTransaction(transactionDTO);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied.");
    }


}
