package com.backend.bankingapp.repositories;

import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByPrimaryOwner(AccountHolder owner);
    List<Account> findBySecondaryOwner(AccountHolder owner);
    Optional<Account> findById(Long id);

}
