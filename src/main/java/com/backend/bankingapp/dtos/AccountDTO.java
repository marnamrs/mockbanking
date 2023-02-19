package com.backend.bankingapp.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AccountDTO {
    @Positive
    @NotNull
    private double doubleBalance;
    @Positive
    @NotNull
    private Long primaryOwnerId;
    private Long secondaryOwnerId;
    //savingsAccount can be instantiated with minBalance < default && > 100
    private Double minBalance;
    //savingsAccount can be instantiated with intRate between 0-0.5 (default is 0.0025)
    //creditCards can be instantiated with intRate between 0.1-0.2 (default is 0.2)
    private Double interestRate;
    //creditCards can be instantiated with creditLimit between 100-100.000 (default is 100)
    private Double creditLimit;


    //checkingAccount, studentAccount, Savings(default), creditCard(default)
    public AccountDTO(double doubleBalance, Long primaryOwnerId) {
        setDoubleBalance(doubleBalance);
        setPrimaryOwnerId(primaryOwnerId);
    }



}
