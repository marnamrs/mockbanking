package com.backend.bankingapp.controllers.interfaces;

import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.users.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface AdminControllerInterface {

    //GET:User
    List<User> getUsers();
    User getUserById(Long id);

    //GET:ThirdParty
    List<ThirdParty> getExternals();
    ThirdParty getExternalById(Long id);

    //GET: Accounts
    List<Account> getAccounts();
    Account getAccountById(Long id);

    //POST: Users
    User createUser(UserDTO userDTO);

    //POST: ThirdParty
    ThirdParty createExternal(String name);

    //POST: Accounts [Creation]
    Account createCheckingAccount(AccountDTO accountDTO);
    Account createSavingsAccount(AccountDTO accountDTO);
    Account createCreditCard(AccountDTO accountDTO);

    //POST: Accounts [Updating]
    Account setAccountBalance(Long accountId, double amount);


}
