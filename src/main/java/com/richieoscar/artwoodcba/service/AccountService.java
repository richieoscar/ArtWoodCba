package com.richieoscar.artwoodcba.service;

import com.richieoscar.artwoodcba.dto.*;

public interface AccountService {


    DefaultApiResponse openAccount(OpenAccountDTO openAccountDTO);
    DefaultApiResponse deposit(DepositDTO depositDTO);

    DefaultApiResponse withdraw(WithdrawDTO withdrawDTO);
    DefaultApiResponse balance(BalanceDTO withdrawDTO);
    DefaultApiResponse activate(ActivateDTO withdrawDTO);
    DefaultApiResponse close(CloseAccountDTO withdrawDTO);
    DefaultApiResponse transfer(TransferDTO withdrawDTO);
    DefaultApiResponse history(String customerId, String accountId, int page, int size);


}
