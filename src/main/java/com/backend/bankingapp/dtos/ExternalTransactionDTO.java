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
public class ExternalTransactionDTO {
    @NotNull
    @Positive
    private Long beneficiaryAccountId;
    @NotNull
    @NotEmpty
    private String beneficiaryKey;
    @NotNull
    private double amount;
}
