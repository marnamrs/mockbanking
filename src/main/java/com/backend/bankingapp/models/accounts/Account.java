package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.users.User;
import com.backend.bankingapp.models.utils.Money;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private Money balance;
    //TODO check how to map ManyToMany primary&secondary owner
//    @ManyToMany
//    private AccountHolder primaryOwner;
//    @ManyToMany
//    private AccountHolder secondaryOwner;
    @ManyToMany
    private List<AccountHolder> owners = new ArrayList<>();
    private Money penaltyFee;
    private LocalDate creationDate;
    private LocalDate lastAccessDate;
    //TODO add List<Transaction>

    public Account(Money balance){
        setBalance(balance);
        //default penaltyFee
        setPenaltyFee(new Money(new BigDecimal("40.00")));
        setCreationDate();
        setLastAccessDate(creationDate);
    }

    public void setCreationDate(){
        //default: set according to CET timezone
        ZoneId zone = ZoneId.of("Europe/Madrid");
        creationDate = LocalDate.now(zone);
    }
    public void setLastAccessDate(){
        //default: set according to CET timezone
        ZoneId zone = ZoneId.of("Europe/Madrid");
        creationDate = LocalDate.now(zone);
    }
    public void setLastAccessDate(LocalDate date){
        //override default set if date needs to be manually set
        this.lastAccessDate = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return getId() == account.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
