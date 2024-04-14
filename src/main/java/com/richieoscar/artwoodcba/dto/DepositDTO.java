package com.richieoscar.artwoodcba.dto;

import java.math.BigDecimal;

public record DepositDTO(String customerId, String accountId, BigDecimal amount) {
}
