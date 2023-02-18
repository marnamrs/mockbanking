package com.backend.bankingapp.repositories.accountrepos;

import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.accounts.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckingAccountRepository extends JpaRepository<CheckingAccount, Long> {
    Optional<CheckingAccount> findById(Long id);
    Optional<CheckingAccount> findByAccountKey(String accountKey);
}
