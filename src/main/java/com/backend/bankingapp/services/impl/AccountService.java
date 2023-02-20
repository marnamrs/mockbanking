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
        log.info("Executing transaction between account {} and {}", originator.getId(), receiver.getId());
        if(originator.getStatus() == Status.ACTIVE && receiver.getStatus() == Status.ACTIVE){
            log.info("Executing transation: verified both accounts are Active");
            BigDecimal originatorFunds = originator.getBalance().getAmount();
            BigDecimal transactionAmount = BigDecimal.valueOf(transactionDTO.getAmount());
            //verify sufficient funds (balance>amount)
            if(originatorFunds.compareTo(transactionAmount)>0){
                log.info("Verified sufficient funds.");
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

    public Transaction createExternalTransaction(ExternalTransactionDTO transactionDTO, Account account){
        //verify accounts status is Active
        if(account.getStatus().equals(Status.ACTIVE)){
            BigDecimal transactionAmount = BigDecimal.valueOf(transactionDTO.getAmount());
            Transaction transaction = new Transaction(account, new Money(transactionAmount));
            //verify if potential fraud
            Boolean isFraudulent = verifyFraud(transaction, account);
            if(!isFraudulent){
                //check sign of transaction
                if(transactionDTO.getAmount() < 0){
                    BigDecimal accountFunds = account.getBalance().getAmount();
                    //subtraction of funds: requires sufficient funds verification
                    if(accountFunds.compareTo(transactionAmount)>0){
                        //all checks passed: execute
                        return executeTransaction(transaction, account);
                    }
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: insufficient funds.");
                }
                //addition of funds does not require sufficient funds verification
                log.info("Executing transaction {}", transaction.getId());
                return executeTransaction(transaction, account);
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: potential fraudulent activity detected.");
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: account is frozen.");
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
        Transaction saved = transactionRepository.save(transaction);
        originator.getSentTransactions().add(saved);
        receiver.getReceivedTransactions().add(saved);
        accountRepository.save(originator);
        accountRepository.save(receiver);

        log.info("Transaction saved to database.");

        //apply penaltyFee if necessary for classes with minBalance
//        if(checkingAccountRepository.findById(originatorId).isPresent()){
//            CheckingAccount acc = checkingAccountRepository.findById(originatorId).get();
//            acc.verifyPenaltyFee(prevBalanceOriginator, postBalanceOriginator);
//            accountRepository.save(acc);
//        }
//        if(savingsAccountRepository.findById(originatorId).isPresent()){
//            SavingsAccount acc = savingsAccountRepository.findById(originatorId).get();
//            acc.verifyPenaltyFee(prevBalanceOriginator, postBalanceOriginator);
//            accountRepository.save(acc);
//        }

        return transaction;
    }

    private Transaction executeTransaction(Transaction transaction, Account account){
        log.info("Executing transaction by third party");

        Long accountId = account.getId();
        BigDecimal prevBalance = account.getBalance().getAmount();
        BigDecimal transactionAmount = transaction.getAmount().getAmount();

        //calculate new balance
        BigDecimal postBalance = prevBalance.add(transactionAmount);
        //set new balance
        account.setBalance(new Money(postBalance));
        //save to database
        transactionRepository.save(transaction);
        accountRepository.save(account);

        //apply penaltyFee if necessary for classes with minBalance when detracting funds
        if(prevBalance.compareTo(postBalance)>0){
            if(checkingAccountRepository.findById(accountId).isPresent()){
                CheckingAccount acc = checkingAccountRepository.findById(accountId).get();
                acc.verifyPenaltyFee(prevBalance, postBalance);
                accountRepository.save(acc);
            }
            if(savingsAccountRepository.findById(accountId).isPresent()){
                SavingsAccount acc = savingsAccountRepository.findById(accountId).get();
                acc.verifyPenaltyFee(prevBalance, postBalance);
                accountRepository.save(acc);
            }
        }
        return transaction;
    }

    private Boolean verifyFraud(Transaction transaction, Account account) {
        log.info("Checking for fraudulent activity");
        // Fraud case: >2 transactions within a 1-second period
        boolean isFraudulent = false;

        //get all transactions
        List<Transaction> previousSentTransactions = account.getSentTransactions();
        List<Transaction> previousReceivedTransactions = account.getSentTransactions();

        //check for fraud if account list of transactions > 1
        if(previousSentTransactions.size()>1){
            //get time of last transaction
            LocalDateTime lastTransferTime = previousSentTransactions.get(previousSentTransactions.size()-1).getCreationDate();
            //get time of current transaction
            LocalDateTime currentTransferTime = transaction.getCreationDate();
            LocalDateTime thisTransferMinusOneSec = currentTransferTime.minusSeconds(1);
            //true if last transfer was less than 1'' before current transfer
            if(lastTransferTime.isAfter(thisTransferMinusOneSec)){
                isFraudulent = true;
            }
        }
        if(previousReceivedTransactions.size()>1){
            LocalDateTime lastTransferTime = previousReceivedTransactions.get(previousReceivedTransactions.size()-1).getCreationDate();
            LocalDateTime currentTransferTime = transaction.getCreationDate();
            LocalDateTime thisTransferMinusOneSec = currentTransferTime.minusSeconds(1);
            //true if last transfer was less than 1'' before current transfer
            if(lastTransferTime.isAfter(thisTransferMinusOneSec)){
                isFraudulent = true;
            }
        }

        //if isFraudulent == true, freeze account
        if(isFraudulent){
            log.info("Fraudulent activity detected: account {} status set to Frozen", account.getId());
            account.setStatus(Status.FROZEN);
            accountRepository.save(account);
        }
        return isFraudulent;
    }

    public String checkAccountType(Account account) {
        CheckingAccount checkingAccount = new CheckingAccount();
        StudentAccount studentAccount = new StudentAccount();
        SavingsAccount savingsAccount = new SavingsAccount();
        CreditCard creditCard = new CreditCard();
        String checkingType = checkingAccount.getClass().getTypeName();
        String studentType = studentAccount.getClass().getTypeName();
        String savingsType = savingsAccount.getClass().getTypeName();
        String creditType = creditCard.getClass().getTypeName();

        String accountType = account.getClass().getTypeName();

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
