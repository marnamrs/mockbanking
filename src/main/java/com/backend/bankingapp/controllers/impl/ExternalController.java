package com.backend.bankingapp.controllers.impl;

import com.backend.bankingapp.dtos.ExternalTransactionDTO;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.utils.Transaction;
import com.backend.bankingapp.services.impl.AccountService;
import com.backend.bankingapp.services.impl.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external")
public class ExternalController {

    @Autowired
    private ExternalService externalService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public ThirdParty getExternal(@RequestHeader("access-key") String key, String name){
        return externalService.getExternalByName(name, key);
    }

    @PostMapping("/transaction/new")
    @ResponseStatus(HttpStatus.OK)
    public Transaction newTransaction(@RequestHeader("access-key") String key, @RequestBody ExternalTransactionDTO externalTransactionDTO){
        return externalService.newTransaction(key, externalTransactionDTO);
    }
}
