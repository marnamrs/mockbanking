package com.backend.bankingapp.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    private Long primaryOwnerId;
    @Positive
    private Long secondaryOwnerId;
    //savingsAccount can be instantiated with minBalance < default && > 100
    @DecimalMin(value = "100")
    private double minBalance;
    @DecimalMax(value="0.5")
    @PositiveOrZero
    private double interestRateSavings;



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
//    public AccountDTO(double doubleBalance, Long primaryOwnerId, Long secondaryOwnerId, double minBalance, double interestRateSavings) {
//        setDoubleBalance(doubleBalance);
//        setPrimaryOwnerId(primaryOwnerId);
//        setSecondaryOwnerId(secondaryOwnerId);
//        setMinBalance(minBalance);
//        setInterestRateSavings(interestRateSavings);
//    }

}
