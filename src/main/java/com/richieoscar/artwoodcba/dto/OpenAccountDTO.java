package com.richieoscar.artwoodcba.dto;

import jakarta.validation.constraints.NotBlank;

public record OpenAccountDTO(@NotBlank String customerId, @NotBlank String accountType, boolean allowOverDraft, String currency, String productId) {
}
