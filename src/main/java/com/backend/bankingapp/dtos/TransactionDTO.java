package com.backend.bankingapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionDTO {

    private Long originatorKey;
    private Long beneficiaryKey;
    private double amount;
}
