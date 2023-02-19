package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.ExternalTransactionDTO;
import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.models.accounts.*;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import com.backend.bankingapp.models.utils.Transaction;
import com.backend.bankingapp.repositories.accountrepos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Autowired
    private TransactionRepository transactionRepository;

    //update: : apply any pending fees/interests
    public Account update(Account account){
        log.info("Updating account {}", account.getId());
        Account acc = accountRepository.findById(account.getId()).get();
        acc.update();
        return accountRepository.save(acc);
    }

    public void updateAll(List<Account> accounts){
        log.info("Updating {} accounts", accounts.size());
        for(Account a : accounts){
            a.update();
            accountRepository.save(a);
        }
    }

    public Transaction createTransaction(TransactionDTO transactionDTO, Account originator, Account receiver) {
        //verify accounts status is Active
        if(originator.getStatus() == Status.ACTIVE && receiver.getStatus() == Status.ACTIVE){
            BigDecimal originatorFunds = originator.getBalance().getAmount();
            BigDecimal transactionAmount = BigDecimal.valueOf(transactionDTO.getAmount());
            //verify sufficient funds (balance>amount)
            if(originatorFunds.compareTo(transactionAmount)>0){
                Transaction transaction = new Transaction(originator, receiver, new Money(transactionAmount));
                //verify if potential fraud
                Boolean isFraudulent = verifyFraud(transaction, originator);
                if(!isFraudulent){
                    //when all checks are passed, call executeTransaction()
                    log.info("Executing transaction {}", transaction.getId());
                    return executeTransaction(transaction, originator, receiver);
                }
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: potential fraudulent activity detected.");
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: insufficient funds.");
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: account/s are frozen.");
    }

    public Transaction createExternalTransaction(ExternalTransactionDTO transactionDTO){
        //TODO add createTransaction (External version) in AccService
        return null;
    }

    private Transaction executeTransaction(Transaction transaction, Account originator, Account receiver){
        log.info("Executing transaction between account {} and account {}", originator.getId(), receiver.getId());

        Long originatorId = originator.getId();
        BigDecimal prevBalanceOriginator = originator.getBalance().getAmount();
        BigDecimal prevBalanceReceiver = receiver.getBalance().getAmount();
        BigDecimal transactionAmount = transaction.getAmount().getAmount();

        //calculate new balances
        BigDecimal postBalanceOriginator = prevBalanceOriginator.subtract(transactionAmount);
        BigDecimal postBalanceReceiver = prevBalanceReceiver.add(transactionAmount);

        //set new balances
        originator.setBalance(new Money(postBalanceOriginator));
        receiver.setBalance(new Money(postBalanceReceiver));
        //save to database
        transactionRepository.save(transaction);
        accountRepository.save(originator);
        accountRepository.save(receiver);
        log.info("Transaction saved to database.");

        //apply penaltyFee if necessary for classes with minBalance
        if(checkingAccountRepository.findById(originatorId).isPresent()){
            CheckingAccount acc = checkingAccountRepository.findById(originatorId).get();
            acc.verifyPenaltyFee(prevBalanceOriginator, postBalanceOriginator);
            accountRepository.save(acc);
        }
        if(savingsAccountRepository.findById(originatorId).isPresent()){
            SavingsAccount acc = savingsAccountRepository.findById(originatorId).get();
            acc.verifyPenaltyFee(prevBalanceOriginator, postBalanceOriginator);
            accountRepository.save(acc);
        }

        return transaction;
    }


    private Boolean verifyFraud(Transaction transaction, Account originator) {
        // Fraud case: >2 transactions within a 1-second period
        boolean isFraudulent = false;

        //get all account transactions as originator
        List<Transaction> previousTransactions = originator.getExpenseTransactions();

        //check for fraud if account transactions > 1
        if(previousTransactions.size()>1){
            LocalDateTime lastTransferTime = previousTransactions.get(previousTransactions.size()-1).getCreationDate();
            LocalDateTime currentTransferTime = transaction.getCreationDate();
            LocalDateTime thisTransferMinusOneSec = currentTransferTime.minusSeconds(1);
            //true if last transfer was less than 1'' before current transfer
            isFraudulent = lastTransferTime.isAfter(thisTransferMinusOneSec);
        }

        //if isFraudulent == true, freeze account
        if(isFraudulent){
            log.info("Fraudulent activity detected: account {} status set to Frozen", originator.getId());
            originator.setStatus(Status.FROZEN);
            accountRepository.save(originator);
        }
        return isFraudulent;
    }

    private String checkAccountType(Account originator) {
        CheckingAccount checkingAccount = new CheckingAccount();
        StudentAccount studentAccount = new StudentAccount();
        SavingsAccount savingsAccount = new SavingsAccount();
        CreditCard creditCard = new CreditCard();
        String checkingType = checkingAccount.getClass().getTypeName();
        String studentType = studentAccount.getClass().getTypeName();
        String savingsType = savingsAccount.getClass().getTypeName();
        String creditType = creditCard.getClass().getTypeName();

        String accountType = originator.getClass().getTypeName();

        if (accountType.equals(checkingType)) {
            return checkingType;
        } else if (accountType.equals(studentType)) {
            return studentType;
        } else if (accountType.equals(savingsType)) {
            return savingsType;
        } else if (accountType.equals(creditType)) {
            return creditType;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: account type not found.");
    }

}
