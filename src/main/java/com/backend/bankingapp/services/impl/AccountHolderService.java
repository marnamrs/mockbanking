package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.repositories.AccountRepository;
import com.backend.bankingapp.repositories.RoleRepository;
import com.backend.bankingapp.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class AccountHolderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;

    public User getUserInfo(Authentication user) {
        log.info("Fetching user {}", user.getPrincipal());
        return userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
    }

    public BigDecimal getBalance(Authentication user){
        log.info("Fetching global balance for user {}", user.getPrincipal());
        AccountHolder owner = (AccountHolder) userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
        BigDecimal sum = new BigDecimal("0");
        //Get all accounts of user
        List<Account> acc1 = owner.getPrimaryAccounts();
        List<Account> acc2 = owner.getSecondaryAccounts();
        List<Account> accounts = Stream.concat(acc1.stream(), acc2.stream()).toList();
        //Update and get balance for each account
        for(Account a : accounts){
            System.out.println("------- balance pre-update: " + a.getBalance().getAmount());
            a.update();
            System.out.println("------- balance post-update: " + a.getBalance().getAmount());
            sum = sum.add(a.getBalance().getAmount());
        }
        log.info("Global balance for user {} is {}", user.getPrincipal(), sum);
        return sum;
    }


}
