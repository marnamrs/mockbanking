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
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "balance_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "balance_amount")),
    })
    private Money balance;
    @ManyToOne
    private AccountHolder primaryOwner;
    @ManyToOne
    private AccountHolder secondaryOwner;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "penalty_fee_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "penalty_fee_amount")),
    })
    private Money penaltyFee;
    private LocalDate creationDate;
    private LocalDate lastAccessDate;

    //OneToMany <--- transfers
    //TODO add List<Transaction> x2 (received and sent)

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
        //override: set given date
        this.lastAccessDate = date;
    }

    //subclasses need to define update method for fees and interests
    //update() gets called for each accountHolder.getBalance()
    public abstract void update();

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
