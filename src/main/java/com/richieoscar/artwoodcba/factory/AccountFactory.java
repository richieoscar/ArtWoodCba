package com.richieoscar.artwoodcba.factory;

import com.richieoscar.artwoodcba.domain.Account;
import com.richieoscar.artwoodcba.dto.OpenAccountDTO;
import com.richieoscar.artwoodcba.dto.enums.AccountType;
import com.richieoscar.artwoodcba.dto.enums.CurrencyType;
import com.richieoscar.artwoodcba.dto.enums.Status;
import com.richieoscar.artwoodcba.exception.IdGenException;
import com.richieoscar.artwoodcba.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountFactory {
    private final AccountNumberGenerator generator;

    public Account createAccount(OpenAccountDTO openAccountDTO) {
        try {
            AccountType accountType = AccountType.valueOf(openAccountDTO.accountType());
            switch (accountType) {
                case SAVINGS -> {
                    return createSavingsAccount(openAccountDTO);
                }
                case LOAN -> {return createLaonAccount(openAccountDTO);}
                case CURRENT -> {return createCurrentAccount(openAccountDTO);}
            }
        } catch (IdGenException | Exception e) {
            log.error("Error occurred with IdGen {}", e.getMessage());
        }
        return null;

    }

    private Account createSavingsAccount(OpenAccountDTO openAccountDTO) throws IdGenException {
        Account account = new Account();
        account.setAccountType(AccountType.SAVINGS);
        account.setAccountBalance(BigDecimal.ZERO);
        account.setAccountNumber(generator.generateAccountNumber());
        account.setCurrency(CurrencyType.valueOf(openAccountDTO.currency()));
        account.setOpeningDate(LocalDateTime.now());
        account.setStatus(Status.PENDING_ACTIVATION);
        account.setLienApplied(false);
        return account;
    }

    private Account createLaonAccount(OpenAccountDTO openAccountDTO) throws IdGenException {
        Account account = new Account();
        account.setAccountType(AccountType.LOAN);
        account.setAccountBalance(BigDecimal.ZERO);
        account.setAccountNumber(generator.generateAccountNumber());
        account.setCurrency(CurrencyType.valueOf(openAccountDTO.currency()));
        account.setOpeningDate(LocalDateTime.now());
        account.setStatus(Status.PENDING_ACTIVATION);
        account.setLienApplied(false);
        return account;
    }

    private Account createCurrentAccount(OpenAccountDTO openAccountDTO) throws IdGenException {
        Account account = new Account();
        account.setAccountType(AccountType.CURRENT);
        account.setAccountBalance(BigDecimal.ZERO);
        account.setAccountNumber(generator.generateAccountNumber());
        account.setCurrency(CurrencyType.valueOf(openAccountDTO.currency()));
        account.setOpeningDate(LocalDateTime.now());
        account.setStatus(Status.PENDING_ACTIVATION);
        account.setLienApplied(false);
        return account;
    }
}
