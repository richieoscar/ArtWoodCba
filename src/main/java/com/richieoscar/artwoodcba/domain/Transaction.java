package com.richieoscar.artwoodcba.domain;

import com.richieoscar.artwoodcba.dto.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionRef;

    @Column
    private String customerId;
    @Column
    private String accountId;

    @Enumerated
    private TransactionType transactionType;

    private LocalDateTime transactionDate;
    @Column
    private BigDecimal amount;

    private BigDecimal runningBalance;

    private BigDecimal balanceAfterRunningBalance;

    private BigDecimal credit;

    private BigDecimal debit;
}
