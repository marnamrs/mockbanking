package com.backend.bankingapp.models.users;

import com.backend.bankingapp.models.utils.Address;
import jakarta.persistence.*;
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
    //TODO add List<Account> to AccountHolder
    //private List<Account>;

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
    //TODO check balance by client
}
