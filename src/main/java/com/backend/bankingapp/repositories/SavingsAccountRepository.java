package com.backend.bankingapp.repositories;

import com.backend.bankingapp.models.accounts.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findById(Long id);
    Optional<SavingsAccount> findByAccountKey(String accountKey);
    //TODO add findByAccountKey() to all account repositories
}