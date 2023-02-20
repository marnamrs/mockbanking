package com.backend.bankingapp;

import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.*;
import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.users.Admin;
import com.backend.bankingapp.models.users.ThirdParty;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Type;
import com.backend.bankingapp.repositories.accountrepos.AccountRepository;
import com.backend.bankingapp.repositories.usersrepos.ThirdPartyRepository;
import com.backend.bankingapp.repositories.usersrepos.UserRepository;

import com.backend.bankingapp.services.impl.AdminService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class
)
public class AdminControllerTest {

    @Autowired
    WebApplicationContext context;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    UserRepository userRepository;
    @Autowired
    ThirdPartyRepository thirdPartyRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AdminService adminService;

    private AccountHolder client1;
    private AccountHolder client2;
    private AccountHolder client3;
    private CheckingAccount account1;
    private CheckingAccount account2;
    private CheckingAccount account3;

    @BeforeEach
    void setUp(){

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        client1 = adminService.createClient(new UserDTO("Client1 Client1", "client1", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
        client2 = adminService.createClient(new UserDTO("Client2 Client2", "client2", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
        client3 = adminService.createClient(new UserDTO("Client3 Client3", "client3", "1234", "ROLE_CLIENT","01/01/1900", "Spain", "Barcelona", "Street", 10, 10001, null, null, null, 0, 0));
        account1 = (CheckingAccount) adminService.newCheckingAccount(new AccountDTO(500, client1.getId()));
        account2 = (CheckingAccount) adminService.newCheckingAccount(new AccountDTO(300, client1.getId()));
        account3 = (CheckingAccount) adminService.newCheckingAccount(new AccountDTO(600, client2.getId()));
    }

    @AfterEach
    void tearDown(){
        accountRepository.deleteById(account1.getId());
        accountRepository.deleteById(account2.getId());
        accountRepository.deleteById(account3.getId());
        userRepository.deleteById(client1.getId());
        userRepository.deleteById(client2.getId());
        if(userRepository.findUserById(client3.getId()).isPresent()){
            userRepository.deleteById(client3.getId());
        }

    }

    @Test
    void getUserById_returnsUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/users/id?id=" + client1.getId()))
                .andExpect(status().isOk())
                .andExpect(result1 -> result1.getResponse().getContentAsString())
                .andReturn();
        AccountHolder resultUser = objectMapper.readValue(result.getResponse().getContentAsString(), AccountHolder.class);
        assertEquals(resultUser.hashCode(), client1.hashCode());
    }
    @Test
    void getAccountById_returnsAccount() throws Exception {
        Long accountId = account1.getId();

        MvcResult result = mockMvc.perform(get("/api/admin/accounts/id?id=" + accountId))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(accountId, objectMapper.readValue(res.getResponse().getContentAsString(), CheckingAccount.class).getId()))
                .andReturn();
    }
    @Test
    void createUser_savesNewAccountHolder_whenRoleNameIsROLE_CLIENT() throws Exception {
        UserDTO userDTO = new UserDTO("Name Name", "testuser", "1234", "ROLE_CLIENT", "01/01/1900", "Spain", "Barcelona", "Test Boulevard", 99, 10010, null, null, null, 0, 0);
        String body = objectMapper.writeValueAsString(userDTO);
        MvcResult result = mockMvc.perform(post("/api/admin/users/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        User user = objectMapper.readValue(result.getResponse().getContentAsString(), AccountHolder.class);
        assertTrue(userRepository.findById(user.getId()).isPresent());
        assertEquals("ROLE_CLIENT", user.getRole().getName());
        userRepository.deleteById(user.getId());
    }
    @Test
    void createUser_savesNewAdmin_whenRoleNameIsROLE_ADMIN() throws Exception {
        UserDTO userDTO = new UserDTO("Admin Admin", "testadmin", "1234", "ROLE_ADMIN");
        String body = objectMapper.writeValueAsString(userDTO);
        MvcResult result = mockMvc.perform(post("/api/admin/users/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(userRepository.findByUsername("testadmin").isPresent());
        User user = objectMapper.readValue(result.getResponse().getContentAsString(), Admin.class);
        assertEquals("ROLE_ADMIN", user.getRole().getName());
        userRepository.deleteById(user.getId());
    }
    @Test
    void createExternal_savesNewThirdParty() throws Exception {
        String name = "externalName";
        String body = objectMapper.writeValueAsString(name);
        MvcResult result = mockMvc.perform(post("/api/admin/externals/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        ThirdParty user = objectMapper.readValue(result.getResponse().getContentAsString(), ThirdParty.class);
        assertTrue(thirdPartyRepository.findById(user.getId()).isPresent());
        thirdPartyRepository.deleteById(user.getId());
    }
    @Test
    void createChecking_savesNewChecking_whenUserOver24() throws Exception {
        Long userId = client1.getId();
        AccountDTO accountDTO = new AccountDTO(500, userId);
        String accBody = objectMapper.writeValueAsString(accountDTO);
        MvcResult resultAccount = mockMvc.perform(post("/api/admin/accounts/add/checking").content(accBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        Account account = objectMapper.readValue(resultAccount.getResponse().getContentAsString(), CheckingAccount.class);
        assertEquals(Type.CHECKING.hashCode(), account.getType().hashCode());
        assertEquals(userId, account.getPrimaryOwner().getId());
        accountRepository.deleteById(account.getId());
    }
    @Test
    void createChecking_savesNewStudent_whenUserUnder24() throws Exception {
        UserDTO userDTO = new UserDTO("Student Student", "testuser", "1234", "ROLE_CLIENT", "01/01/2020", "Spain", "Barcelona", "Test Boulevard", 99, 10010, null, null, null, 0, 0);
        String body = objectMapper.writeValueAsString(userDTO);
        MvcResult resultUser = mockMvc.perform(post("/api/admin/users/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        User user = objectMapper.readValue(resultUser.getResponse().getContentAsString(), AccountHolder.class);
        assertTrue(userRepository.findById(user.getId()).isPresent());

        AccountDTO accountDTO = new AccountDTO(500, user.getId());
        String accBody = objectMapper.writeValueAsString(accountDTO);
        MvcResult resultAccount = mockMvc.perform(post("/api/admin/accounts/add/checking").content(accBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        Account account = objectMapper.readValue(resultAccount.getResponse().getContentAsString(), StudentAccount.class);
        assertEquals(Type.STUDENT.hashCode(), account.getType().hashCode());
        accountRepository.deleteById(account.getId());
        userRepository.deleteById(user.getId());
    }
    @Test
    void createSaving_savesNewSavingsAccount() throws Exception {
        UserDTO userDTO = new UserDTO("Adult Adult", "testuser", "1234", "ROLE_CLIENT", "01/01/1950", "Spain", "Barcelona", "Test Boulevard", 99, 10010, null, null, null, 0, 0);
        String body = objectMapper.writeValueAsString(userDTO);
        MvcResult resultUser = mockMvc.perform(post("/api/admin/users/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(userRepository.findByUsername("testuser").isPresent());
        User user = objectMapper.readValue(resultUser.getResponse().getContentAsString(), AccountHolder.class);

        AccountDTO accountDTO = new AccountDTO(600, user.getId(), null, 500.00, null, null);
        String accBody = objectMapper.writeValueAsString(accountDTO);
        MvcResult resultAccount = mockMvc.perform(post("/api/admin/accounts/add/savings").content(accBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        SavingsAccount account = objectMapper.readValue(resultAccount.getResponse().getContentAsString(), SavingsAccount.class);
        assertEquals(Type.SAVINGS.hashCode(), account.getType().hashCode());
        assertEquals(account.getPrimaryOwner().getId(), user.getId());
        assertEquals(account.getMinimumBalance().getAmount(), new BigDecimal("500.00"));
        accountRepository.deleteById(account.getId());
        userRepository.deleteById(user.getId());
    }
    @Test
    void createCredit_savesNewCreditCard() throws Exception {
        UserDTO userDTO = new UserDTO("Adult Adult", "testuser", "1234", "ROLE_CLIENT", "01/01/1950", "Spain", "Barcelona", "Test Boulevard", 99, 10010, null, null, null, 0, 0);
        String body = objectMapper.writeValueAsString(userDTO);
        MvcResult resultUser = mockMvc.perform(post("/api/admin/users/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(userRepository.findByUsername("testuser").isPresent());
        User user = objectMapper.readValue(resultUser.getResponse().getContentAsString(), AccountHolder.class);

        AccountDTO accountDTO = new AccountDTO(600, user.getId(), null, null, null, 900.00);
        String accBody = objectMapper.writeValueAsString(accountDTO);
        MvcResult resultAccount = mockMvc.perform(post("/api/admin/accounts/add/credit").content(accBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CreditCard account = objectMapper.readValue(resultAccount.getResponse().getContentAsString(), CreditCard.class);
        assertEquals(Type.CREDIT.hashCode(), account.getType().hashCode());
        assertEquals(account.getPrimaryOwner().getId(), user.getId());
        assertEquals(account.getCreditLimit().getAmount(), new BigDecimal("900.00"));
        accountRepository.deleteById(account.getId());
        userRepository.deleteById(user.getId());
    }
    @Test
    void setAccountBalance_editsBalance() throws Exception {
        double newBalance = 300.00;
        Money expected = new Money(new BigDecimal(newBalance));
        Long accountId = account1.getId();
        assertNotEquals(0, account1.getBalance().getAmount().compareTo(new BigDecimal(newBalance)));
        MvcResult resultAccount = mockMvc.perform(put("/api/admin/accounts/update/balance?accountId=" + accountId + "&amount=" + newBalance))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(accountId, objectMapper.readValue(res.getResponse().getContentAsString(), CheckingAccount.class).getId()))
                .andExpect(res -> assertEquals(expected.getAmount(), objectMapper.readValue(res.getResponse().getContentAsString(), CheckingAccount.class).getBalance().getAmount()))
                .andReturn();
    }
    @Test
    void deleteUser_deletesUserById() throws Exception {
        Long userId = client3.getId();
        assertTrue(userRepository.findById(userId).isPresent());
        mockMvc.perform(delete("/api/admin/users/delete/id?id=" + userId));
        assertFalse(userRepository.findById(userId).isPresent());
    }

}
