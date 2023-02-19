package com.backend.bankingapp.models.utils;

import com.backend.bankingapp.models.accounts.Account;
import jakarta.persistence.*;
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
    private Money amount;

    private LocalDateTime executionDate;

    //internal Transactions
    public Transaction(Account originator, Account receiver, Money amount){
        setOriginator(originator);
        setReceiver(receiver);
        setAmount(amount);
        setExecutionDate();
    }

    //ThirdParty Transactions
    public Transaction(Account receiver, Money amount){
        setReceiver(receiver);
        setAmount(amount);
        setExecutionDate();
    }

    public void setExecutionDate(){
        //default: set according to CET timezone
        ZoneId zone = ZoneId.of("Europe/Madrid");
        executionDate = LocalDateTime.now(zone);
    }
    public void setExecutionDate(LocalDateTime date){
        //override: set given date
        this.executionDate = date;
    }

}
