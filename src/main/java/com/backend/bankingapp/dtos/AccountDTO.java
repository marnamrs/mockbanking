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
    private double doubleBalance;
    @Positive
    @NotNull
    @NotEmpty
    private Long primaryOwnerId;
    @Positive
    private Long secondaryOwnerId;
    //savingsAccount can be instantiated with minBalance < default && > 100

    private Double minBalance;
    //savingsAccount can be instantiated with intRate between 0-0.5 (default is 0.2)
    private Double interestRateSavings;


    //checkingAccount, studentAccount, Savings(default)
    public AccountDTO(double doubleBalance, Long primaryOwnerId) {
        setDoubleBalance(doubleBalance);
        setPrimaryOwnerId(primaryOwnerId);
    }
    public AccountDTO(double doubleBalance, Long primaryOwnerId, Long secondaryOwnerId) {
        setDoubleBalance(doubleBalance);
        setPrimaryOwnerId(primaryOwnerId);
        setSecondaryOwnerId(secondaryOwnerId);
    }
    //saving accounts(non-default)
    public AccountDTO(double doubleBalance, Long primaryOwnerId, double minBalance, double interestRateSavings) {
        setDoubleBalance(doubleBalance);
        setPrimaryOwnerId(primaryOwnerId);
        setMinBalance(minBalance);
        setInterestRateSavings(interestRateSavings);
    }


}
