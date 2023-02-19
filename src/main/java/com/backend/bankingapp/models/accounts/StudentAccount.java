package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.HashCreator;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import com.backend.bankingapp.models.utils.Type;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentAccount extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String accountKey = setAccountKey();

    public StudentAccount(Money balance, AccountHolder primaryOwner, Type type) {
        super(balance, primaryOwner, type);
    }
    public StudentAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Type type) {
        super(balance, primaryOwner, secondaryOwner, type);
    }

    @Override
    public void update() {
        ChronoLocalDate twentyFourYearsAgo = ChronoLocalDate.from(LocalDate.now().minusYears(24));
        if(getPrimaryOwner().getBirthDate().isBefore(twentyFourYearsAgo)){
            throw new ResponseStatusException(HttpStatus.UPGRADE_REQUIRED, "Account owner does not qualify for Student Checking. Please update Account type to CheckingAccount.");
        }
    }

    private String setAccountKey(){
        return HashCreator.createAccountKey();
    }
}
