package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.ExternalTransactionDTO;
import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.accounts.CheckingAccount;
import com.backend.bankingapp.models.accounts.SavingsAccount;
import com.backend.bankingapp.models.accounts.StudentAccount;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.utils.Transaction;
import com.backend.bankingapp.models.utils.Type;
import com.backend.bankingapp.repositories.accountrepos.AccountRepository;
import com.backend.bankingapp.repositories.accountrepos.CheckingAccountRepository;
import com.backend.bankingapp.repositories.accountrepos.SavingsAccountRepository;
import com.backend.bankingapp.repositories.accountrepos.StudentAccountRepository;
import com.backend.bankingapp.repositories.usersrepos.ThirdPartyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExternalService {
    @Autowired
    private SavingsAccountRepository savingsAccountRepository;
    @Autowired
    private StudentAccountRepository studentAccountRepository;
    @Autowired
    private CheckingAccountRepository checkingAccountRepository;
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


    public Transaction newTransaction(String userKey, ExternalTransactionDTO transactionDTO) {
        log.info("Verifying external party access key");
        //verify access key
        if(verifyKey(userKey)){
            log.info("Successful access key verification");

            //verify receiver account exists by id
            Long receiverId = transactionDTO.getReceiverAccountId();
            String receiverKey = transactionDTO.getReceiverAccountKey();
            if(accountRepository.findAccountById(receiverId).isPresent()){
                log.info("Valid account Id");
                Account account = accountRepository.findAccountById(receiverId).get();

                //filter down to account types using accountKey
                if(account.getType().equals(Type.CHECKING) || account.getType().equals(Type.STUDENT) || account.getType().equals(Type.SAVINGS)){
                    log.info("Valid account type");

                    //validate accessKey
                    String key = transactionDTO.getReceiverAccountKey();
                    boolean keyMatch = false;

                    //find by key in account type repository and check match
                    if(account.getType().equals(Type.CHECKING) && checkingAccountRepository.findByAccountKey(key).isPresent()){
                        CheckingAccount acc = (CheckingAccount) account;
                        //check match between id and accountKey
                        if(acc.getId() == checkingAccountRepository.findByAccountKey(key).get().getId()){ keyMatch = true;}
                    }
                    if(account.getType().equals(Type.STUDENT) && studentAccountRepository.findByAccountKey(key).isPresent()){
                        StudentAccount acc2 = (StudentAccount) account;
                        if(acc2.getId() == studentAccountRepository.findByAccountKey(key).get().getId()){ keyMatch = true;}
                    }
                    if(account.getType().equals(Type.SAVINGS) && savingsAccountRepository.findByAccountKey(key).isPresent()){
                        SavingsAccount acc3 = (SavingsAccount) account;
                        if(acc3.getId() == savingsAccountRepository.findByAccountKey(key).get().getId()){ keyMatch = true;}
                    }

                    if(keyMatch){
                        //update account
                        Account updatedAccount = accountService.update(account);
                        /*Redirect to accountService.createExternalTransaction() for account-side checks:
                         * -- sufficient funds verification
                         * -- valid amount verification
                         * If all verifications are successful:
                         * -- accountService.executeTransaction() will be called
                         * -- executed Transaction object will be returned
                         */
                        return accountService.createExternalTransaction(transactionDTO, updatedAccount);
                    } else {
                        log.info("Transaction not possible: mismatch between id and accountKey");
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not possible: account not found.");
                    }

                }
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Transaction not possible: account does not allow third party transactions (CreditCard).");
            }

            //update receiver account
            //create Transaction
            /*Redirect to accountService.createTransaction() for account-side checks:
             * -- sufficient funds verification
             * -- valid amount verification
             * If all verifications are successful:
             * -- accountService.executeTransaction() will be called
             * -- executed Transaction object will be returned
             */

            Optional<Account> receiver = accountRepository.findById(transactionDTO.getReceiverAccountId());
//            accountService.createTransaction(transactionDTO);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied.");
    }


}
