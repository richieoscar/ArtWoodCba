package com.richieoscar.artwoodcba.dto;

import jakarta.validation.constraints.NotBlank;

public record ActivateDTO(@NotBlank String accountId, @NotBlank String customerId, String note) {
}
