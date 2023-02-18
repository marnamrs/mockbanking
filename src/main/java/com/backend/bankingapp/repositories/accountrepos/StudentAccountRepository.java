package com.backend.bankingapp.repositories.accountrepos;

import com.backend.bankingapp.models.accounts.StudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentAccountRepository extends JpaRepository<StudentAccount, Long> {
    Optional<StudentAccount> findById(Long id);
    Optional<StudentAccount> findByAccountKey(String accountKey);
}
