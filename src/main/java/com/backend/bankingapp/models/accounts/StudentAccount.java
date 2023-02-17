package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.HashCreator;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAccount extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String accountKey = setAccountKey();
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public StudentAccount(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
    }
    public StudentAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, primaryOwner, secondaryOwner);
    }

    @Override
    public void update() {
        ChronoLocalDate twentyFourYearsAgo = ChronoLocalDate.from(LocalDate.now().minusYears(24));
        if(getPrimaryOwner().getBirthDate().isBefore(twentyFourYearsAgo)){
            throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED, "Account owner does not qualify for Student Checking anymore. Please update Account type to CheckingAccount.");
        }
    }

    private String setAccountKey(){
        return HashCreator.createAccountKey();
    }
}
