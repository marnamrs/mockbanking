package com.backend.bankingapp.models.utils;

import com.backend.bankingapp.models.accounts.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    @ManyToOne
//    private Account originatorAccount;
//    @ManyToOne
//    private Account beneficiaryAccount;

//    ManyToOne --> account

    //localdatetime
}
