package com.backend.bankingapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionDTO {
    @NotNull
    @Positive
    private Long originatorAccountId;
    @NotNull
    @NotEmpty
    private String receiverAccountOwner;
    @NotNull
    @Positive
    private Long receiverAccountId;
    @NotNull
    @Positive
    private double amount;

}
