package com.backend.bankingapp;

import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.TransactionDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.accounts.CheckingAccount;
import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.Transaction;
import com.backend.bankingapp.repositories.accountrepos.AccountRepository;
import com.backend.bankingapp.repositories.accountrepos.TransactionRepository;
import com.backend.bankingapp.repositories.usersrepos.UserRepository;
import com.backend.bankingapp.services.impl.AccountHolderService;
import com.backend.bankingapp.services.impl.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class
)
public class AccountHolderTest {

    @Autowired
    WebApplicationContext context;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AdminService adminService;
    @Autowired
    AccountHolderService accountHolderService;

    private AccountHolder client1;
    private AccountHolder client2;
    private AccountHolder client3;
    private CheckingAccount account1;
    private CheckingAccount account2;
    private CheckingAccount account3;


    @BeforeEach
    void setUp(){

        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        client1 = adminService.createClient(new UserDTO("Client1 Client1", "client1", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
        client2 = adminService.createClient(new UserDTO("Client2 Client2", "client2", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
        client3 = adminService.createClient(new UserDTO("Client3 Client3", "client3", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
        account1 = (CheckingAccount) adminService.newCheckingAccount(new AccountDTO(500, client1.getId()));
        account2 = (CheckingAccount) adminService.newCheckingAccount(new AccountDTO(300, client1.getId()));
        account3 = (CheckingAccount) adminService.newCheckingAccount(new AccountDTO(600, client2.getId()));
    }

    @AfterEach
    void tearDown(){
//        accountRepository.deleteById(account1.getId());
//        accountRepository.deleteById(account2.getId());
//        accountRepository.deleteById(account3.getId());
//
//        userRepository.deleteById(client1.getId());
//        userRepository.deleteById(client2.getId());
//        userRepository.deleteById(client3.getId());

    }

    @Test
    void newTransaction_createsAndExecutesTransaction(){
        Account originator = account1;
        String username = client1.getUsername();
        TransactionDTO transactionDTO = new TransactionDTO(originator.getId(), client2.getName(), account3.getId(), 55);
        BigDecimal originatorBalancePre = account1.getBalance().getAmount();
        BigDecimal receiverBalancePre = account3.getBalance().getAmount();
        Transaction transaction = accountHolderService.newTransaction(username, transactionDTO);
        Account originatorUpdated = accountRepository.findAccountById(account1.getId()).get();
        Account receiverUpdated = accountRepository.findAccountById(account3.getId()).get();
        assertTrue(originatorUpdated.getBalance().getAmount().compareTo(originatorBalancePre)<0);
        assertTrue(receiverUpdated.getBalance().getAmount().compareTo(receiverBalancePre)>0);
    }

}
