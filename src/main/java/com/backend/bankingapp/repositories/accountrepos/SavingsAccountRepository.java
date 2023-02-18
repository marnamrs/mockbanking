package com.backend.bankingapp.repositories.accountrepos;

import com.backend.bankingapp.models.accounts.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findById(Long id);
    Optional<SavingsAccount> findByAccountKey(String accountKey);

}
