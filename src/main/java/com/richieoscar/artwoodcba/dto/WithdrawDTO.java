package com.richieoscar.artwoodcba.dto;

import java.math.BigDecimal;

public record WithdrawDTO(String customerId, String accountId, BigDecimal amount) {
}
