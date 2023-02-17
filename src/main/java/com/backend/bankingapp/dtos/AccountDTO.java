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
}
