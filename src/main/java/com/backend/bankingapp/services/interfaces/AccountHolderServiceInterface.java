package com.backend.bankingapp.services.interfaces;

import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.models.utils.Transaction;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface AccountHolderServiceInterface {

    //GET: own user info
    User getUserInfo(Authentication user);
    //GET: all accounts of user
    List<Account> getAccounts(Authentication user);
    //GET: account info
    Account getAccountById(Authentication user, Long accountId);
    //GET: global balance (all accounts)
    BigDecimal getBalance(Authentication user);
    //POST: new transaction
    Transaction newTransaction(String username, TransactionDTO transactionDTO);
}
