package com.richieoscar.artwoodcba.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record TransferDTO(@NotBlank String sourceAccountId, @NotBlank String sourceCustomerId, @NotBlank String destinationAccountId, @NotBlank String destinationCustomerId, BigDecimal amount, boolean self) {
}
