package com.richieoscar.artwoodcba.controller;

import com.richieoscar.artwoodcba.dto.*;
import com.richieoscar.artwoodcba.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/open")
    public ResponseEntity<DefaultApiResponse> openAccount(@RequestBody @Valid OpenAccountDTO openAccountDTO) {
        return ResponseEntity.ok(accountService.openAccount(openAccountDTO));
    }

    @PostMapping("/deposit")
    public ResponseEntity<DefaultApiResponse> deposit(@RequestBody @Valid DepositDTO depositDTO) {
        return ResponseEntity.ok(accountService.deposit(depositDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<DefaultApiResponse> withdraw(@RequestBody @Valid WithdrawDTO withdrawDTO) {
        return ResponseEntity.ok(accountService.withdraw(withdrawDTO));
    }

    @PostMapping("/transfer")
    public ResponseEntity<DefaultApiResponse> transfer(@RequestBody @Valid TransferDTO transferDTO) {
        return ResponseEntity.ok(accountService.transfer(transferDTO));
    }

    @PostMapping("/balance")
    public ResponseEntity<DefaultApiResponse> balance(@RequestBody @Valid BalanceDTO balanceDTO) {
        return ResponseEntity.ok(accountService.balance(balanceDTO));
    }

    @PostMapping("/activate")
    public ResponseEntity<DefaultApiResponse> activate(@RequestBody @Valid ActivateDTO activateDTO) {
        return ResponseEntity.ok(accountService.activate(activateDTO));
    }

    @PostMapping("/close")
    public ResponseEntity<DefaultApiResponse> close(@RequestBody @Valid CloseAccountDTO closeAccountDTO) {
        return ResponseEntity.ok(accountService.close(closeAccountDTO));
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<DefaultApiResponse> close(@PathVariable("customerId") String customerId, @RequestParam(value = "accountId", required = false) String accountId,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(accountService.history(customerId, accountId, page, size));
    }
}
