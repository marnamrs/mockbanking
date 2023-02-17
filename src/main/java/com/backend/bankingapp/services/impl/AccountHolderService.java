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
import java.util.Objects;
import java.util.Optional;
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

    public List<Account> getAccounts(Authentication user){
        log.info("Fetching accounts for user {}", user.getPrincipal());
        AccountHolder u = (AccountHolder) userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
        List<Account> acc1 = u.getPrimaryAccounts();
        List<Account> acc2 = u.getSecondaryAccounts();
        return Stream.concat(acc1.stream(), acc2.stream()).toList();
    }

    public Account getAccountById(Authentication user, Long accountId){
        log.info("Fetching account {} for user {}", accountId, user.getPrincipal());
        AccountHolder u = (AccountHolder) userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
        Optional<Account> account = accountRepository.findById(accountId);
        //verify if account exists
        if(account.isPresent()){
            Long userId = u.getId();
            Long primaryOwnerId = account.get().getPrimaryOwner().getId();
            Long secondaryOwnerId = account.get().getSecondaryOwner().getId();
            //verify if user owns account
            if(Objects.equals(userId, primaryOwnerId) || Objects.equals(userId, secondaryOwnerId)){
                return account.get();
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Permission denied.");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    };

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
            //apply any pending fees before checking balance
            a.update();
            accountRepository.save(a);
            //TODO another loop to get updated balance
            sum = sum.add(a.getBalance().getAmount());
        }
        log.info("Global balance for user {} is {}", user.getPrincipal(), sum);
        return sum;
    }


}
