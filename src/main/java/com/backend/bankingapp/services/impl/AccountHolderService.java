package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.models.utils.Transaction;
import com.backend.bankingapp.repositories.accountrepos.AccountRepository;
import com.backend.bankingapp.repositories.usersrepos.RoleRepository;
import com.backend.bankingapp.repositories.usersrepos.UserRepository;
import com.backend.bankingapp.services.interfaces.AccountHolderServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class AccountHolderService implements AccountHolderServiceInterface {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    public User getUserInfo(Authentication user) {
        log.info("Fetching user {}", user.getPrincipal());
        return userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
    }

    public List<Account> getAccounts(Authentication user){
        log.info("Fetching accounts for user {}", user.getPrincipal());
        AccountHolder u = (AccountHolder) userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
        List<Account> acc1 = u.getPrimaryAccounts();
        List<Account> acc2 = u.getSecondaryAccounts();
        List<Account> acc = Stream.concat(acc1.stream(), acc2.stream()).toList();
        //update accounts
        accountService.updateAll(acc);
        //fetch updated accounts and return all
        List<Account> updated1 = u.getPrimaryAccounts();
        List<Account> updated2 = u.getSecondaryAccounts();
        return Stream.concat(updated1.stream(), updated2.stream()).toList();
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
                //update account and return
                return accountService.update(account.get());
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
        //Update accounts and fetch updated
        accountService.updateAll(accounts);
        List<Account> upd1 = owner.getPrimaryAccounts();
        List<Account> upd2 = owner.getSecondaryAccounts();
        List<Account> updAccounts = Stream.concat(upd1.stream(), upd2.stream()).toList();
        //sum balance for each account
        for(Account a : updAccounts){
            sum = sum.add(a.getBalance().getAmount());
        }
        log.info("Global balance for user {} is {}", user.getPrincipal(), sum);
        return sum;
    }

    public Transaction newTransaction(Authentication user, TransactionDTO transactionDTO) {
        log.info("Fetching user {} and transaction request", user.getPrincipal());
        AccountHolder u = (AccountHolder) userRepository.findByUsername(String.valueOf(user.getPrincipal())).get();
        Optional<Account> originator = accountRepository.findById(transactionDTO.getOriginatorAccountId());
        Optional<Account> receiver = accountRepository.findById(transactionDTO.getReceiverAccountId());

        //verify both accounts exist
        if(originator.isPresent() && receiver.isPresent()){
            log.info("Verifying ownership of originator account {}", originator.get().getId());
            //verify user owns originator account
            Long userId = u.getId();
            Long primaryOwnerId = originator.get().getPrimaryOwner().getId();
            Long secondaryOwnerId = originator.get().getSecondaryOwner().getId();
            if(Objects.equals(userId, primaryOwnerId) || Objects.equals(userId, secondaryOwnerId)){
                log.info("Ownership of originator account successfully verified.");
                //verify receiver account owner matches given name
                if(receiver.get().getPrimaryOwner().getName().equalsIgnoreCase(transactionDTO.getReceiverAccountOwner()) || receiver.get().getSecondaryOwner().getName().equalsIgnoreCase(transactionDTO.getReceiverAccountOwner())){
                    //update originator
                    Account updatedOriginator = accountService.update(originator.get());
                    /*Redirect to accountService.createTransaction() for account-side checks:
                     * -- sufficient funds verification
                     * -- valid amount verification
                     * If all verifications are successful:
                     * -- accountService.executeTransaction() will be called
                     * -- executed Transaction object will be returned
                     */
                    return accountService.createTransaction(transactionDTO, updatedOriginator, receiver.get());
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not possible: receiver account information does not match provided recipient name.");
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Transaction not possible: failed authorization check.");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not possible: account/s id not found.");
    }

}
