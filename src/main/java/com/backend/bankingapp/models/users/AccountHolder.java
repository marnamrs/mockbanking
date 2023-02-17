package com.backend.bankingapp.models.users;

import com.backend.bankingapp.models.accounts.Account;
import com.backend.bankingapp.models.utils.Address;
import com.backend.bankingapp.models.utils.Money;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolder extends User{
    private LocalDate birthDate;
    @Embedded
    private Address primaryAddress;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="country", column = @Column(name = "mailing_country")),
            @AttributeOverride(name="city", column = @Column(name = "mailing_city")),
            @AttributeOverride(name="street", column = @Column(name = "mailing_street")),
            @AttributeOverride(name="streetNum", column = @Column(name = "mailing_street_num")),
            @AttributeOverride(name="zipCode", column = @Column(name = "mailing_zip_code"))
    })
    private Address mailingAddress;

    @OneToMany(mappedBy = "primaryOwner")
    @JsonIgnore
    private List<Account> primaryAccounts = new ArrayList<>();
    @OneToMany(mappedBy = "secondaryOwner")
    @JsonIgnore
    private List<Account> secondaryAccounts = new ArrayList<>();

    //single address
    public AccountHolder(String name, String username, String password, Role role, LocalDate birthDate, Address primaryAddress) {
        super(name, username, password, role);
        setBirthDate(birthDate);
        setPrimaryAddress(primaryAddress);
    }
    //primary and secondary address
    public AccountHolder(String name, String username, String password, Role role, LocalDate birthDate, Address primaryAddress, Address mailingAddress) {
        super(name, username, password, role);
        setBirthDate(birthDate);
        setPrimaryAddress(primaryAddress);
        setMailingAddress(mailingAddress);
    }

    //TODO add transfer method

    //TODO move getBalance() to service (! should update())

}
