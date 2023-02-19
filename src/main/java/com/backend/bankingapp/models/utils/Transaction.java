package com.backend.bankingapp.models.utils;

import com.backend.bankingapp.models.accounts.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Account originator;
    @ManyToOne
    private Account receiver;
    @Embedded
    @Positive
    private Money amount;

    private LocalDateTime creationDate;

    //internal Transactions
    public Transaction(Account originator, Account receiver, Money amount){
        setOriginator(originator);
        setReceiver(receiver);
        setAmount(amount);
        setCreationDate();
    }

    //ThirdParty Transactions
    public Transaction(Account receiver, Money amount){
        setReceiver(receiver);
        setAmount(amount);
        setCreationDate();
    }

    public void setCreationDate(){
        //default: set according to CET timezone
        ZoneId zone = ZoneId.of("Europe/Madrid");
        creationDate = LocalDateTime.now(zone);
    }
    public void setCreationDate(LocalDateTime date){
        //override: set given date
        this.creationDate = date;
    }

}
