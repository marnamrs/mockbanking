package com.backend.bankingapp.models.users;

import com.backend.bankingapp.models.utils.Address;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolder extends User{
    @NotNull
    @NotEmpty
    private LocalDate birthDate;
    @ManyToOne
    private Address primaryAddress;
    @ManyToOne
    private Address mailingAddress;
    //TODO add List<Account> to AccountHolder
    //private List<Account>;

    public AccountHolder(String name, String username, String password, Role role, LocalDate birthDate, Address primaryAddress, Address mailingAddress) {
        super(name, username, password, role);
        setBirthDate(birthDate);
        setPrimaryAddress(primaryAddress);
        setMailingAddress(mailingAddress);
    }
    //TODO add transfer method
    //TODO check balance by client
}
