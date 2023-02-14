package com.backend.bankingapp.models.users;

import jakarta.persistence.Embedded;
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
    //private List<Account>;

    public AccountHolder(String name, String username, String password, Role role, LocalDate birthDate, Address primaryAddress, Address mailingAddress) {
        super(name, username, password, role);
        setBirthDate(birthDate);
        setPrimaryAddress(primaryAddress);
        setMailingAddress(mailingAddress);
    }
}
