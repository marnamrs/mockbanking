package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.accounts.CheckingAccount;
import com.backend.bankingapp.models.accounts.StudentAccount;
import com.backend.bankingapp.models.users.*;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.UserFactory;
import com.backend.bankingapp.repositories.*;
import com.backend.bankingapp.services.interfaces.AdminServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements AdminServiceInterface, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CheckingAccountRepository checkingAccountRepository;
    @Autowired
    private StudentAccountRepository studentAccountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findByUsername(username).isEmpty()) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            User user = userRepository.findByUsername(username).get();
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    @Override
    public User createUser(UserDTO userDTO) {
        log.info("Creating new user {} with role {}", userDTO.getName(), userDTO.getRoleName());
        if (roleRepository.findByName(userDTO.getRoleName()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found in the database");
        }
        Role role = roleRepository.findByName(userDTO.getRoleName()).get();
        return saveUser(UserFactory.createUser(userDTO, role));
    }

    public AccountHolder createClient(UserDTO userDTO) {
        log.info("Creating new user {} with role Client", userDTO.getName());
        if (roleRepository.findByName("ROLE_CLIENT").isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found in the database");
        }
        Role role = roleRepository.findByName("ROLE_CLIENT").get();
        User user = UserFactory.createUser(userDTO, role);
        return (AccountHolder) saveUser(user);
    }

    public ThirdParty createExternal(String name){
        log.info("Creating access for external party {}.", name);
        Role role = null;
        if(roleRepository.findByName("ROLE_EXTERNAL").isPresent()){
            role = roleRepository.findByName("ROLE_EXTERNAL").get();
        }
        ThirdParty user = UserFactory.createExternal(name, role);
        //logging key for testing/verification purposes
        log.info("Generated key for user {}: {}", user.getName(), user.getAccessKey());
        return saveExternal(user);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public ThirdParty saveExternal(ThirdParty user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setAccessKey(passwordEncoder.encode(user.getAccessKey()));
        return thirdPartyRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        if(userRepository.findByUsername(username).isPresent() && roleRepository.findByName(roleName).isPresent()){
            User user = userRepository.findByUsername(username).get();
            Role role = roleRepository.findByName(roleName).get();
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User/Role not found");
        }

    }
    @Override
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        if(userRepository.findByUsername(username).isPresent()){
            return userRepository.findByUsername(username).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public User getUserById(Long id) {
        log.info("Fetching user {}", id);
        if(userRepository.findUserById(id).isPresent()){
            return userRepository.findUserById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public ThirdParty getExternalById(Long id) {
        log.info("Fetching external {}", id);
        if(thirdPartyRepository.findById(id).isPresent()){
            return thirdPartyRepository.findById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "External party not found");
    }

    @Override
    public List<ThirdParty> getExternals() {
        log.info("Fetching all third parties.");
        return thirdPartyRepository.findAll();
    }

    //creation of CheckingAccount or StudentAccount
    @Override
    public Account newCheckingAccount(AccountDTO accountDTO) {
        //verify if user exists
        Long userId = accountDTO.getPrimaryOwnerId();
        double initialBalance = accountDTO.getDoubleBalance();
        if(userRepository.findUserById(userId).isPresent()){

            //verify user is AccountHolder type
            String className = "com.backend.bankingapp.models.users.AccountHolder";
            if(userRepository.findUserById(userId).get().getClass().getName().equals(className)){

                AccountHolder primary = (AccountHolder) userRepository.findUserById(userId).get();
                Money balance = new Money(new BigDecimal(initialBalance));

                //check if user age < 24
                ChronoLocalDate twentyFourYearsAgo = ChronoLocalDate.from(LocalDate.now().minusYears(24));
                if(primary.getBirthDate().isAfter(twentyFourYearsAgo)){
                    log.info("User {} is eligible for StudentChecking", primary.getName());
                    log.info("Creating new StudentAccount of user {}", primary.getName());
                    Account studentAcc = new StudentAccount(balance, primary);
                    log.info("Saving new StudentAccount {} to the database", studentAcc.getId());
                    return accountRepository.save(studentAcc);
                }

                log.info("Creating new CheckingAccount of user {}", primary.getName());
                Account account = new CheckingAccount(balance, primary);
                log.info("Saving new CheckingAccount {} to the database", account.getId());
                return accountRepository.save(account);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User/AccountHolder not found");
    }

    @Override
    public Account setBalance(Long accountId, double newBalance) {
        if(accountRepository.findAccountById(accountId).isPresent()){
            Account account = accountRepository.findAccountById(accountId).get();
            //update before executing operation
            account.update();
            BigDecimal prevBalance = account.getBalance().getAmount();
            account.setBalance(new Money(new BigDecimal(newBalance)));
            BigDecimal postBalance = account.getBalance().getAmount();
            accountRepository.save(account);
            //apply penaltyFee if necessary for classes with minBalance
            if(checkingAccountRepository.findById(accountId).isPresent()){
                CheckingAccount acc = checkingAccountRepository.findById(accountId).get();
                acc.verifyPenaltyFee(prevBalance, postBalance);
                account = acc;
            }
            return account;
            //TODO add penaltyFee check for Savings
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(Long id) {
        log.info("Fetching account {}", id);
        if(accountRepository.findAccountById(id).isPresent()){
            return accountRepository.findAccountById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
}
