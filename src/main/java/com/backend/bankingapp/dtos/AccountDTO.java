package com.backend.bankingapp.dtos;

import jakarta.validation.constraints.Positive;
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

    public AccountDTO(double doubleBalance, Long primaryOwnerId) {
        setDoubleBalance(doubleBalance);
        setPrimaryOwnerId(primaryOwnerId);
    }
//    public AccountDTO(double doubleBalance, Long primaryOwnerId, Long secondaryOwnerId) {
//        setDoubleBalance(doubleBalance);
//        setPrimaryOwnerId(primaryOwnerId);
//        setSecondaryOwnerId(secondaryOwnerId);
//    }
}
