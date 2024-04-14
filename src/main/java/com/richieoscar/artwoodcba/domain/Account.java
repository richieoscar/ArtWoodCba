package com.richieoscar.artwoodcba.domain;

import com.richieoscar.artwoodcba.dto.enums.AccountType;
import com.richieoscar.artwoodcba.dto.enums.CurrencyType;
import com.richieoscar.artwoodcba.dto.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private LocalDateTime approvedDate;
    private boolean lienApplied;

    @ManyToOne
    private Customer customer;

    @Version
    private long version;

    private BigDecimal accountBalance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime openingDate;

    private LocalDateTime closingDate;
    private LocalDateTime activatedDate;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    private LocalDateTime balanceLastModified;


}
