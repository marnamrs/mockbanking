package com.backend.bankingapp.models.accounts;

import com.backend.bankingapp.models.users.AccountHolder;
import com.backend.bankingapp.models.utils.Money;
import com.backend.bankingapp.models.utils.Status;
import com.backend.bankingapp.models.utils.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @OneToMany(mappedBy = "originator")
    private List<Transaction> expenseTransactions;
    @OneToMany(mappedBy = "receiver")
    private List<Transaction> incomeTransactions;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="currency", column = @Column(name = "penalty_fee_currency")),
            @AttributeOverride(name="amount", column = @Column(name = "penalty_fee_amount")),
    })
    private Money penaltyFee = new Money(new BigDecimal("40"));
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    //default DateTime
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastUpdated = LocalDateTime.now();


    public Account(Money balance){
        setBalance(balance);
        setCreationDate();
        setLastUpdated(creationDate);
    }
    public Account(Money balance, AccountHolder primaryOwner){
        setBalance(balance);
        setPrimaryOwner(primaryOwner);
        setCreationDate();
        setLastUpdated(creationDate);
    }
    public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner){
        setBalance(balance);
        setPrimaryOwner(primaryOwner);
        setSecondaryOwner(secondaryOwner);
        setCreationDate();
        setLastUpdated(creationDate);
    }


    public void setCreationDate(){
        //default: set according to CET timezone
        ZoneId zone = ZoneId.of("Europe/Madrid");
        creationDate = LocalDateTime.now(zone);
    }
    public void setLastUpdated(){
        //default: set according to CET timezone
        ZoneId zone = ZoneId.of("Europe/Madrid");
        creationDate = LocalDateTime.now(zone);
    }
    public void setLastUpdated(LocalDateTime date){
        //override: set given date
        this.lastUpdated = date;
    }

    //subclasses need to define update method for fees and interests
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
