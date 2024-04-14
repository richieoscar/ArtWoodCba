package com.richieoscar.artwoodcba.service.impl;

import com.richieoscar.artwoodcba.domain.Account;
import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.domain.Transaction;
import com.richieoscar.artwoodcba.dto.*;
import com.richieoscar.artwoodcba.dto.enums.Status;
import com.richieoscar.artwoodcba.dto.enums.TransactionType;
import com.richieoscar.artwoodcba.exception.AccountException;
import com.richieoscar.artwoodcba.exception.BalanceException;
import com.richieoscar.artwoodcba.exception.CustomerException;
import com.richieoscar.artwoodcba.factory.AccountFactory;
import com.richieoscar.artwoodcba.repository.AccountRepository;
import com.richieoscar.artwoodcba.repository.CustomerRepository;
import com.richieoscar.artwoodcba.repository.TransactionRepository;
import com.richieoscar.artwoodcba.service.AccountService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountFactory accountFactory;
    private final TransactionRepository transactionRepository;


    @Override
    public DefaultApiResponse openAccount(OpenAccountDTO openAccountDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        Optional<Customer> customer = customerRepository.findByCustomerId(openAccountDTO.customerId());
        if (customer.isEmpty()) throw new CustomerException("Customer not found");
        Account account = accountFactory.createAccount(openAccountDTO);
        if (account != null) {
            account.setCustomer(customer.get());
            account = accountRepository.save(account);
            OpenAccountRespinseDTO data = new OpenAccountRespinseDTO(account.getAccountNumber(), String.valueOf(customer.get().getCustomerId()), account.getOpeningDate().toString());
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Account Opened Successfully");
            defaultApiResponse.setData(data);
        } else {
            defaultApiResponse.setStatus("99");
            defaultApiResponse.setMessage("Failed to Open Account");
        }
        return defaultApiResponse;
    }

    @Override
    @Transactional
    public DefaultApiResponse deposit(DepositDTO depositDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        Optional<Customer> customer = customerRepository.findByCustomerId(depositDTO.customerId());
        if (customer.isEmpty()) throw new CustomerException("Customer not found");
        Optional<Account> account = accountRepository.findByAccountNumberAndCustomer_Id(depositDTO.accountId(), customer.get().getId());
        if (account.isEmpty()) throw new AccountException("Customer with accountId not found");
        if (account.get().getStatus() != Status.ACTIVE) throw new AccountException("Customer Account not Active");
        try {
            makeDeposit(account.get(), depositDTO.amount(), customer.get().getCustomerId());
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Approved");
        } catch (OptimisticLockException e) {
            log.error("Error occurred depositing to account {}", e.getEntity());
            defaultApiResponse.setStatus("70");
            defaultApiResponse.setMessage("Concurrent modification attempted on account");
        } catch (Exception e) {
            defaultApiResponse.setStatus("99");
            defaultApiResponse.setMessage("Unable to deposit to account");
        }
        return defaultApiResponse;
    }

    private synchronized Account makeDeposit(Account account, BigDecimal amount, String customerId) {
        log.info("Current Thread in makeDeposit {}", Thread.currentThread().getName());
        BigDecimal currentBalance = account.getAccountBalance();
        BigDecimal newBalance = currentBalance.add(amount);
        account.setAccountBalance(newBalance);
        account.setBalanceLastModified(LocalDateTime.now());
        logTransaction(amount, account.getAccountNumber(), customerId, currentBalance, newBalance, TransactionType.DEPOSIT);
        return accountRepository.save(account);
    }

    private synchronized Account makeWithDraw(Account account, BigDecimal amount, String customerId) {
        log.info("Current Thread in makeWithDraw() {}", Thread.currentThread().getName());
        BigDecimal currentBalance = account.getAccountBalance();
        if (currentBalance.compareTo(amount) < 0) throw new BalanceException("Insufficient Balance");
        BigDecimal newBalance = currentBalance.subtract(amount);
        account.setAccountBalance(newBalance);
        account.setBalanceLastModified(LocalDateTime.now());
        logTransaction(amount, account.getAccountNumber(), customerId, currentBalance, newBalance, TransactionType.WITHDRAW);
        return accountRepository.save(account);
    }

    private void logTransaction(BigDecimal amount, String accountNumber, String customerId, BigDecimal currentBalance, BigDecimal newBalance, TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionRef(UUID.randomUUID().toString());
        transaction.setTransactionType(transactionType);
        transaction.setCustomerId(customerId);
        transaction.setAccountId(accountNumber);
        transaction.setRunningBalance(currentBalance);
        transaction.setBalanceAfterRunningBalance(newBalance);
        transaction.setAmount(amount);
        if (transactionType.equals(TransactionType.DEPOSIT)) {
            transaction.setCredit(amount);
        } else {
            transaction.setDebit(amount);
        }
        transactionRepository.save(transaction);
    }

    @Override
    public DefaultApiResponse withdraw(WithdrawDTO withdrawDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        Optional<Customer> customer = customerRepository.findByCustomerId(withdrawDTO.customerId());
        if (customer.isEmpty()) throw new CustomerException("Customer not found");
        Optional<Account> account = accountRepository.findByAccountNumberAndCustomer_Id(withdrawDTO.accountId(), customer.get().getId());
        if (account.isEmpty()) throw new AccountException("Customer with accountId not found");
        if (account.get().getStatus() != Status.ACTIVE) throw new AccountException("Customer Account not Active");
        try {
            makeWithDraw(account.get(), withdrawDTO.amount(), customer.get().getCustomerId());
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Approved");
        } catch (OptimisticLockException e) {
            log.error("Error occurred depositing to account {}", e.getEntity());
            defaultApiResponse.setStatus("70");
            defaultApiResponse.setMessage("Concurrent modification attempted on account");
        } catch (BalanceException e) {
            log.error("Error occurred depositing to account {}", e.getMessage());
            defaultApiResponse.setStatus("11");
            defaultApiResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            defaultApiResponse.setStatus("99");
            defaultApiResponse.setMessage("Unable to Withdraw from account");
        }
        return defaultApiResponse;
    }

    @Override
    public DefaultApiResponse balance(BalanceDTO balanceDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        record BalanceResponse(BigDecimal balance) {
        }
        Optional<Customer> customer = customerRepository.findByCustomerId(balanceDTO.customerId());
        if (customer.isEmpty()) throw new CustomerException("Customer not found");
        Optional<Account> account = accountRepository.findByAccountNumberAndCustomer_Id(balanceDTO.accountId(), customer.get().getId());
        if (account.isEmpty()) throw new AccountException("Customer with accountId not found");
        if (account.get().getStatus() != Status.ACTIVE) throw new AccountException("Customer Account not Active");
        defaultApiResponse.setStatus("00");
        defaultApiResponse.setMessage("Approved");
        defaultApiResponse.setData(new BalanceResponse(account.get().getAccountBalance()));
        return defaultApiResponse;
    }

    @Override
    public DefaultApiResponse activate(ActivateDTO activateDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        try {
            Optional<Customer> customer = customerRepository.findByCustomerId(activateDTO.customerId());
            if (customer.isEmpty()) throw new CustomerException("Customer not found");
            Optional<Account> account = accountRepository.findByAccountNumberAndCustomer_Id(activateDTO.accountId(), customer.get().getId());
            if (account.isEmpty()) throw new AccountException("Account with accountId not found");
            if (account.get().getStatus() == Status.ACTIVE) throw new AccountException("Account already activated");
            account.get().setActivatedDate(LocalDateTime.now());
            account.get().setStatus(Status.ACTIVE);
            accountRepository.save(account.get());
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Account Activated Successfully");
        } catch (OptimisticLockException e) {
            log.info("Error Occurred activating account {}  ", e.getEntity());
            defaultApiResponse.setStatus("70");
            defaultApiResponse.setMessage("Unable to activate Account");
            e.printStackTrace();
        } catch (AccountException e) {
            log.info("Error Occurred activating account {}  ", e.getMessage());
            defaultApiResponse.setStatus("04");
            defaultApiResponse.setMessage("Account already activated");
            e.printStackTrace();
        } catch (Exception e) {
            defaultApiResponse.setStatus("99");
            defaultApiResponse.setMessage("Unable to activate Account");
            e.printStackTrace();
        }
        return defaultApiResponse;
    }

    @Override
    public DefaultApiResponse close(CloseAccountDTO closeAccountDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        try {
            Optional<Account> account = accountRepository.findByAccountNumber(closeAccountDTO.accountId());
            if (account.isEmpty()) throw new AccountException("Account with accountId not found");
            if (account.get().getStatus() == Status.CLOSED) throw new AccountException("Account already closed");
            account.get().setClosingDate(LocalDateTime.now());
            account.get().setStatus(Status.CLOSED);
            accountRepository.save(account.get());
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Account Activated Successfully");
        } catch (OptimisticLockException e) {
            log.info("Error Occurred activating account {}  ", e.getEntity());
            defaultApiResponse.setStatus("70");
            defaultApiResponse.setMessage("Unable to activate Account");
            e.printStackTrace();
        } catch (Exception e) {
            defaultApiResponse.setStatus("70");
            defaultApiResponse.setMessage("Unable to activate Account");
            e.printStackTrace();
        }
        return defaultApiResponse;
    }

    @Override
    public DefaultApiResponse transfer(TransferDTO transferDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        if (transferDTO.self()) {
            Optional<Customer> customer = customerRepository.findByCustomerId(transferDTO.sourceCustomerId());
            if (customer.isEmpty()) throw new CustomerException("Customer not found");
            Optional<Account> sourceAccount = accountRepository.findByAccountNumberAndCustomer_Id(transferDTO.sourceAccountId(), customer.get().getId());
            if (sourceAccount.isEmpty()) throw new AccountException("Customer with accountId not found");
            Optional<Account> destAccount = accountRepository.findByAccountNumberAndCustomer_Id(transferDTO.destinationAccountId(), customer.get().getId());
            if (destAccount.isEmpty()) throw new AccountException("Customer with destination accountId not found");
            boolean success = makeTransfer(sourceAccount.get(), destAccount.get(), transferDTO.amount(), customer.get().getCustomerId(), customer.get().getCustomerId());
            if (success) {
                defaultApiResponse.setStatus("00");
                defaultApiResponse.setMessage("Self Transfer Successful");
            }
        } else {
            Optional<Customer> srcCustomer = customerRepository.findByCustomerId(transferDTO.sourceCustomerId());
            if (srcCustomer.isEmpty()) throw new CustomerException("Customer sourceId not found");
            Optional<Customer> destCustomer = customerRepository.findByCustomerId(transferDTO.destinationCustomerId());
            if (destCustomer.isEmpty()) throw new CustomerException("Customer with destinationId not found");
            Optional<Account> sourceAccount = accountRepository.findByAccountNumberAndCustomer_Id(transferDTO.sourceAccountId(), srcCustomer.get().getId());
            if (sourceAccount.isEmpty()) throw new AccountException("Customer with source accountId not found");
            if (sourceAccount.get().getStatus() != Status.ACTIVE)
                throw new AccountException("Source Account not active");
            Optional<Account> destAccount = accountRepository.findByAccountNumberAndCustomer_Id(transferDTO.destinationAccountId(), destCustomer.get().getId());
            if (destAccount.isEmpty()) throw new AccountException("Customer with destination accountId not found");
            if (destAccount.get().getStatus() != Status.ACTIVE)
                throw new AccountException("Destination Account not active");

            boolean success = makeTransfer(sourceAccount.get(), destAccount.get(), transferDTO.amount(), srcCustomer.get().getCustomerId(), destCustomer.get().getCustomerId());
            if (success) {
                defaultApiResponse.setStatus("00");
                defaultApiResponse.setMessage("Self Transfer Successful");
            }
        }
        return defaultApiResponse;
    }

    @Override
    public DefaultApiResponse history(String customerId, String accountId, int page, int size) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        Optional<Customer> srcCustomer = customerRepository.findByCustomerId(customerId);
        if (srcCustomer.isEmpty()) throw new CustomerException("Customer not found");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<Transaction> transactions = null;
        if (StringUtils.isNotBlank(accountId)) {
            transactions = transactionRepository.findAllByCustomerIdAndAccountId(customerId, accountId, pageable);
        } else {
            transactions = transactionRepository.findAllByCustomerId(customerId, pageable);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("totalTransactions", transactions.getTotalElements());
        map.put("totalPage", transactions.getTotalPages());
        map.put("items", transactions.getContent());
        transactions.getTotalElements();
        defaultApiResponse.setStatus("00");
        defaultApiResponse.setMessage("Transaction History Retrieved");
        defaultApiResponse.setData(map);
        return defaultApiResponse;
    }

    private synchronized boolean makeTransfer(Account sourceAct, Account destAct, BigDecimal amount, String srcCustomerId, String destCustomerId) {
        try {
            BigDecimal sourceBalance = sourceAct.getAccountBalance();
            BigDecimal destBalance = destAct.getAccountBalance();
            if (sourceBalance.compareTo(amount) < 0) throw new BalanceException("Insufficient Funds");
            BigDecimal newSourceBalance = sourceBalance.subtract(amount);
            BigDecimal newDestBalance = destBalance.add(amount);

            sourceAct.setAccountBalance(newSourceBalance);
            sourceAct.setBalanceLastModified(LocalDateTime.now());

            destAct.setAccountBalance(newDestBalance);
            destAct.setBalanceLastModified(LocalDateTime.now());

            sourceAct = accountRepository.save(sourceAct);
            destAct = accountRepository.save(destAct);

            logTransaction(amount, sourceAct.getAccountNumber(), srcCustomerId, sourceBalance, newSourceBalance, TransactionType.WITHDRAW);
            logTransaction(amount, destAct.getAccountNumber(), destCustomerId, destBalance, newDestBalance, TransactionType.DEPOSIT);

            return true;
        } catch (OptimisticLockException e) {
            log.error("Optimistic Exception thrown  on {}", e.getEntity());
            e.printStackTrace();
            throw new AccountException(e.getMessage());
        }

    }
}
