package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.ExternalTransactionDTO;
import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.utils.Transaction;
import com.backend.bankingapp.repositories.accountrepos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CheckingAccountRepository checkingAccountRepository;
    @Autowired
    private StudentAccountRepository studentAccountRepository;
    @Autowired
    private SavingsAccountRepository savingsAccountRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;

    //update: : apply any pending fees/interests
    public void update(Account account){
        log.info("Updating account {}", account.getId());
        Account acc = accountRepository.findById(account.getId()).get();
        acc.update();
        accountRepository.save(acc);
    }

    public void updateAll(List<Account> accounts){
        log.info("Updating {} accounts", accounts.size());
        for(Account a : accounts){
            a.update();
            accountRepository.save(a);
        }
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) {
        //TODO add createTransaction in AccService
        //fetch info from DTO and create Transaction
        // call service method executeTransaction()
        // then return executed Transaction
        return null;
    }

    public Transaction createExternalTransaction(ExternalTransactionDTO transactionDTO){
        //TODO add createTransaction (External version) in AccService
        return null;
    }

    //TODO add executeTransaction method in AccService
    //executeTransaction(Transaction transaction)
    //update originator and beneficiary
    //get originator initial balance
    //check if funds are sufficient and if so
    //decrease from originator
    //increase to beneficiary
    //add transaction to originator list of transactions
    //get originator new balance
    //if originator is Checking or Savings call originator.applyPenaltyFee()
    //return executed transaction


}
