package com.backend.bankingapp.services.impl;

import com.backend.bankingapp.dtos.AccountDTO;
import com.backend.bankingapp.dtos.UserDTO;
import com.backend.bankingapp.models.accounts.*;
import com.backend.bankingapp.models.users.*;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Type;
import com.backend.bankingapp.models.utils.UserFactory;
import com.backend.bankingapp.repositories.accountrepos.*;
import com.backend.bankingapp.repositories.usersrepos.RoleRepository;
import com.backend.bankingapp.repositories.usersrepos.ThirdPartyRepository;
import com.backend.bankingapp.repositories.usersrepos.UserRepository;
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

    // Utils
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    // Users
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    // Accounts
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


    /*
    [ USER MANAGEMENT: POST ]
    */


    //Post: Users
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
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    //Post: ThirdParty
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
    public ThirdParty saveExternal(ThirdParty user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setAccessKey(passwordEncoder.encode(user.getAccessKey()));
        return thirdPartyRepository.save(user);
    }


    //Post: Roles
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }
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


    /*
    [ USER MANAGEMENT: GET ]
    */


    //Get: Users
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        if(userRepository.findByUsername(username).isPresent()){
            return userRepository.findByUsername(username).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    public User getUserById(Long id) {
        log.info("Fetching user {}", id);
        if(userRepository.findUserById(id).isPresent()){
            return userRepository.findUserById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
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
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }


    //Get: Third Parties
    public ThirdParty getExternalById(Long id) {
        log.info("Fetching external {}", id);
        if(thirdPartyRepository.findById(id).isPresent()){
            return thirdPartyRepository.findById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "External party not found");
    }
    public List<ThirdParty> getExternals() {
        log.info("Fetching all third parties.");
        return thirdPartyRepository.findAll();
    }

    //Delete: Users
    public Void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User {} deleted.", id);
        return null;
    }


    /*
    [ ACCOUNT MANAGEMENT: POST ]
    */


    //Create: CheckingAccount or StudentAccount
    public Account newCheckingAccount(AccountDTO accountDTO) {
        //verify if user exists
        Long userId = accountDTO.getPrimaryOwnerId();

        if(userRepository.findUserById(userId).isPresent()){

            //verify user is AccountHolder type
            String className = "com.backend.bankingapp.models.users.AccountHolder";
            if(userRepository.findUserById(userId).get().getClass().getName().equals(className)){

                AccountHolder primary = (AccountHolder) userRepository.findUserById(userId).get();
                double initialBalance = accountDTO.getDoubleBalance();
                Money balance = new Money(new BigDecimal(initialBalance));

                //check if primary user age < 24
                ChronoLocalDate twentyFourYearsAgo = ChronoLocalDate.from(LocalDate.now().minusYears(24));
                if(primary.getBirthDate().isAfter(twentyFourYearsAgo)){
                    log.info("User {} is eligible for StudentChecking", primary.getName());
                    log.info("Creating new StudentAccount of user {}", primary.getName());
                    StudentAccount studentAcc = new StudentAccount(balance, primary, Type.STUDENT);
                    if(accountDTO.getSecondaryOwnerId() != null && userRepository.findUserById(accountDTO.getSecondaryOwnerId()).isPresent() && userRepository.findUserById(userId).get().getClass().getName().equals(className)){
                        AccountHolder secondary = (AccountHolder) userRepository.findUserById(accountDTO.getSecondaryOwnerId()).get();
                        studentAcc.setSecondaryOwner(secondary);
                    }
                    log.info("Saving new StudentAccount {} to the database", studentAcc.getId());
                    return accountRepository.save(studentAcc);
                }

                log.info("Creating new CheckingAccount of user {}", primary.getName());
                CheckingAccount account = new CheckingAccount(balance, primary, Type.CHECKING);
                if(accountDTO.getSecondaryOwnerId() != null && userRepository.findUserById(accountDTO.getSecondaryOwnerId()).isPresent() && userRepository.findUserById(userId).get().getClass().getName().equals(className)){
                    AccountHolder secondary = (AccountHolder) userRepository.findUserById(accountDTO.getSecondaryOwnerId()).get();
                    account.setSecondaryOwner(secondary);
                }
                if(account.getMinimumBalance().getAmount().compareTo(BigDecimal.valueOf(initialBalance))>0){
                    log.info("Warning: Account created with initial balance below minimum.");
                }
                log.info("Saving new CheckingAccount {} to the database", account.getId());
                return accountRepository.save(account);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User/AccountHolder not found");
    }
    //Create: SavingsAccount
    public Account newSavingsAccount(AccountDTO accountDTO) {

        //verify if user exists
        Long userId = accountDTO.getPrimaryOwnerId();

        if(userRepository.findUserById(userId).isPresent()){

            //verify user is AccountHolder type
            String className = "com.backend.bankingapp.models.users.AccountHolder";
            if(userRepository.findUserById(userId).get().getClass().getName().equals(className)){

                //create account
                AccountHolder primary = (AccountHolder) userRepository.findUserById(userId).get();
                double initialBalance = accountDTO.getDoubleBalance();
                Money balance = new Money(new BigDecimal(initialBalance));

                log.info("Creating new SavingsAccount of user {}", primary.getName());
                SavingsAccount account = new SavingsAccount(balance, primary, Type.SAVINGS);

                //check for optional values and set if informed and valid
                if(accountDTO.getSecondaryOwnerId() != null && userRepository.findUserById(accountDTO.getSecondaryOwnerId()).isPresent() && userRepository.findUserById(userId).get().getClass().getName().equals(className)){
                    AccountHolder secondary = (AccountHolder) userRepository.findUserById(accountDTO.getSecondaryOwnerId()).get();
                    account.setSecondaryOwner(secondary);
                    log.info("Added secondaryOwner to SavingsAccount");
                }
                if(accountDTO.getMinBalance() != null && accountDTO.getMinBalance() > 100){
                    BigDecimal min = BigDecimal.valueOf(accountDTO.getMinBalance());
                    Money minBalance = new Money(min);
                    account.setMinimumBalance(minBalance);
                    log.info("Added non-default minBalance to SavingsAccount");
                }
                if(accountDTO.getInterestRate() != null && accountDTO.getInterestRate()<0.5 && accountDTO.getInterestRate()>0){
                    BigDecimal rate = BigDecimal.valueOf(accountDTO.getInterestRate());
                    account.setInterestRate(rate);
                    log.info("Added non-default interestRate to SavingsAccount");
                }
                if(account.getMinimumBalance().getAmount().compareTo(BigDecimal.valueOf(initialBalance))>0){
                    log.info("Warning: Account created with initial balance below minimum.");
                }
                //save account to database
                log.info("Saving new SavingsAccount {} to the database", account.getId());
                return accountRepository.save(account);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User/AccountHolder not found");
    }
    //Create: CreditCard
    public Account newCreditCard(AccountDTO accountDTO){
        //verify if user exists
        Long userId = accountDTO.getPrimaryOwnerId();

        if(userRepository.findUserById(userId).isPresent()){

            //verify user is AccountHolder type
            String className = "com.backend.bankingapp.models.users.AccountHolder";
            if(userRepository.findUserById(userId).get().getClass().getName().equals(className)){

                //create account
                AccountHolder primary = (AccountHolder) userRepository.findUserById(userId).get();
                double initialBalance = accountDTO.getDoubleBalance();
                Money balance = new Money(new BigDecimal(initialBalance));

                log.info("Creating new CreditCard of user {}", primary.getName());
                CreditCard account = new CreditCard(balance, primary, Type.CREDIT);

                //check for optional values and set if informed and valid
                if(accountDTO.getSecondaryOwnerId() != null && userRepository.findUserById(accountDTO.getSecondaryOwnerId()).isPresent() && userRepository.findUserById(userId).get().getClass().getName().equals(className)){
                    AccountHolder secondary = (AccountHolder) userRepository.findUserById(accountDTO.getSecondaryOwnerId()).get();
                    account.setSecondaryOwner(secondary);
                    log.info("Added secondaryOwner to CreditCard");
                }
                if(accountDTO.getInterestRate() != null && accountDTO.getInterestRate()<0.2 && accountDTO.getInterestRate()>0.1){
                    BigDecimal rate = BigDecimal.valueOf(accountDTO.getInterestRate());
                    account.setInterestRate(rate);
                    log.info("Added non-default interestRate {} to CreditCard", rate);
                }
                if(accountDTO.getCreditLimit() != null && accountDTO.getCreditLimit()<100000 && accountDTO.getCreditLimit()>100){
                    BigDecimal limit = BigDecimal.valueOf(accountDTO.getCreditLimit());
                    account.setCreditLimit(new Money(limit));
                    log.info("Added non-default creditLimit {} to CreditCard", limit);
                }
                //save account to database
                log.info("Saving new CreditCard {} to the database", account.getId());
                return accountRepository.save(account);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User/AccountHolder not found");
    }


    /*
    [ ACCOUNT MANAGEMENT: PUT]
    */
    //Edit: Balance
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
                accountRepository.save(acc);
            }
            if(savingsAccountRepository.findById(accountId).isPresent()){
                SavingsAccount acc = savingsAccountRepository.findById(accountId).get();
                acc.verifyPenaltyFee(prevBalance, postBalance);
                accountRepository.save(acc);
            }
            return account;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }


    /*
    [ ACCOUNT MANAGEMENT: GET ]
    */


    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }
    public Account getAccountById(Long id) {
        log.info("Fetching account {}", id);
        if(accountRepository.findAccountById(id).isPresent()){
            return accountRepository.findAccountById(id).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
    public Account getAccountByKey(String key) {
        if(checkingAccountRepository.findByAccountKey(key).isPresent()){
            return checkingAccountRepository.findByAccountKey(key).get();
        }
        if(studentAccountRepository.findByAccountKey(key).isPresent()){
            return studentAccountRepository.findByAccountKey(key).get();
        }
        if(savingsAccountRepository.findByAccountKey(key).isPresent()){
            return savingsAccountRepository.findByAccountKey(key).get();
        }
        //if nothing was returned during checks:
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Key not found");
    }

}
