package com.backend.bankingapp;

import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.repositories.accountrepos.AccountRepository;
import com.backend.bankingapp.repositories.usersrepos.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AdminControllerTest {

    //Test creation of users
    //Test creation of Account

    @Autowired
    WebApplicationContext context;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createUser_savesNewAccountHolder() throws Exception {
        UserDTO userDTO = new UserDTO("Name Name", "testuser", "1234", "ROLE_CLIENT", "01/01/1900", "Spain", "Barcelona", "Test Boulevard", 99, 10010, null, null, null, 0, 0);
        String body = objectMapper.writeValueAsString(userDTO);
        MvcResult result = mockMvc.perform(post("/api/admin/users/add").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(userRepository.findByUsername("testuser").isPresent());
        User user = userRepository.findByUsername("testuser").get();
        assertEquals("ROLE_CLIENT", user.getRole().getName());
        userRepository.deleteById(user.getId());
    }
}
