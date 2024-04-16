package com.richieoscar.artwoodcba.service.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.richieoscar.artwoodcba.domain.Account;
import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.dto.*;
import com.richieoscar.artwoodcba.dto.enums.Status;
import com.richieoscar.artwoodcba.dto.enums.SystemRole;
import com.richieoscar.artwoodcba.exception.AccountException;
import com.richieoscar.artwoodcba.exception.CustomerException;
import com.richieoscar.artwoodcba.factory.AccountFactory;
import com.richieoscar.artwoodcba.repository.AccountRepository;
import com.richieoscar.artwoodcba.repository.CustomerRepository;
import com.richieoscar.artwoodcba.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = AccountServiceImpl.class)
class AccountServiceImplTest {
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private AccountFactory accountFactory;
    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountServiceImpl accountService;

    private Customer customer;
    private Account account;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setId(2);
        customer.setFirstName("Tolu");
        customer.setPhone("08088998893");
        customer.setLastName("Yolo");
        customer.setRole(SystemRole.CUSTOMER);
        customer.setEmail("boy@yopmail@gmail.com");

        account = new Account();
        account.setAccountNumber("903");
        account.setAccountBalance(BigDecimal.valueOf(500));
        account.setStatus(Status.CLOSED);
        account.setOpeningDate(LocalDateTime.now());
    }

    @DisplayName("test OpenAccount")
    @Test
    void itShouldOpenAccount() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        OpenAccountDTO openAccountDTO = new OpenAccountDTO(customer.getCustomerId(),
                "SAVINGS", false, "NGN", "");
        when(accountFactory.createAccount(openAccountDTO)).thenReturn(account);

        when(accountRepository.save(account)).thenReturn(account);

        DefaultApiResponse response = accountService.openAccount(openAccountDTO);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("00", response.getStatus());

    }

    @DisplayName("test Deposit")
    @Test
    void itShouldDeposit() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        DepositDTO depositDTO = new DepositDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        BigDecimal currentBal = account.getAccountBalance();

        DefaultApiResponse withResponse = accountService.deposit(depositDTO);
        Assertions.assertNotNull(withResponse);
        Assertions.assertEquals("00", withResponse.getStatus());

        Assertions.assertTrue(account.getAccountBalance().compareTo(currentBal) > 0);
    }

    @DisplayName("test deposit and Throw AccountException Exceptions")
    @Test
    void itShouldDepositAndThrowAccountException() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.empty());

        DepositDTO depositDTO = new DepositDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        doThrow(new AccountException("Account not found")).when(accountRepository).findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId());

        Assertions.assertThrows(AccountException.class, () -> accountService.deposit(depositDTO));
    }

    @DisplayName("test deposit and Throw Customer Exceptions")
    @Test
    void itShouldDepositAndThrowCustomerException() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.empty());

        DepositDTO depositDTO = new DepositDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        doThrow(new CustomerException("Customer not found")).when(customerRepository).findByCustomerId(customer.getCustomerId());
        Assertions.assertThrows(CustomerException.class, () -> accountService.deposit(depositDTO));
    }

    @DisplayName("test Withdraw")
    @Test
    void itShouldWithdraw() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        WithdrawDTO withdrawDTO = new WithdrawDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(200));
        BigDecimal currentBal = account.getAccountBalance();

        DefaultApiResponse withResponse = accountService.withdraw(withdrawDTO);

        Assertions.assertNotNull(withResponse);
        Assertions.assertEquals("00", withResponse.getStatus());
        Assertions.assertNotEquals(currentBal.intValue(), account.getAccountBalance().intValue());

    }

    @DisplayName("test withdraw method to Throw Customer Exception")
    @Test
    void itShouldWithdrawThrowCustomerException() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.empty());

        WithdrawDTO withdrawDTO = new WithdrawDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        doThrow(new CustomerException("Customer not found")).when(customerRepository).findByCustomerId(customer.getCustomerId());
        doThrow(new AccountException("Account not found")).when(accountRepository).findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId());

        Assertions.assertThrows(CustomerException.class, () -> accountService.withdraw(withdrawDTO));
    }

    @DisplayName("test Withdraw Will return InsufficientFunds statusCode 11")
    @Test
    void itShouldWithdrawAndReturnInsufficientFunds() {
        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        WithdrawDTO withdrawDTO = new WithdrawDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        DefaultApiResponse withResponse = accountService.withdraw(withdrawDTO);

        Assertions.assertNotNull(withResponse);
        Assertions.assertEquals("11", withResponse.getStatus());

    }

    @DisplayName("test Withdraw Will Throw Exceptions")
    @Test
    void itShouldAttemptWithdraw() {
        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.empty());

        WithdrawDTO withdrawDTO = new WithdrawDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        doThrow(new CustomerException("Customer not found")).when(customerRepository).findByCustomerId(customer.getCustomerId());
        doThrow(new AccountException("Account not found")).when(accountRepository).findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId());

        Assertions.assertThrows(CustomerException.class, () -> accountService.withdraw(withdrawDTO));

    }

    @DisplayName("test Deposit")
    @Test
    void itShouldDepositAndReturnInsufficientFunds() {

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        DepositDTO depositDTO = new DepositDTO(customer.getCustomerId(), account.getAccountNumber(), BigDecimal.valueOf(2000));

        BigDecimal currentBal = account.getAccountBalance();

        DefaultApiResponse withResponse = accountService.deposit(depositDTO);
        Assertions.assertNotNull(withResponse);
        Assertions.assertEquals("00", withResponse.getStatus());

        Assertions.assertTrue(account.getAccountBalance().compareTo(currentBal) > 0);

    }

    @DisplayName("test getAccountBalance")
    @Test
    void itShouldBalance() {
        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        DefaultApiResponse balanceResponse = accountService.balance(new BalanceDTO(customer.getCustomerId(), account.getAccountNumber()));

        Assertions.assertEquals(BigDecimal.valueOf(500).intValue(), account.getAccountBalance().intValue());
        Assertions.assertEquals(Status.ACTIVE, account.getStatus());
        Assertions.assertNotNull(balanceResponse);

        Assertions.assertEquals("00", balanceResponse.getStatus());

        verify(customerRepository, times(1)).findByCustomerId(customer.getCustomerId());
        verify(accountRepository, times(1)).findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId());
    }

    @DisplayName("test close Account")
    @Test
    void itShouldTestClose(){

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        CloseAccountDTO closeDTO = new CloseAccountDTO(customer.getCustomerId(), account.getAccountNumber());

        DefaultApiResponse withResponse = accountService.close(closeDTO);

        Assertions.assertNotNull(withResponse);
        Assertions.assertEquals("00", withResponse.getStatus());
        Assertions.assertEquals(Status.CLOSED, account.getStatus());

    }

    @DisplayName("test activate Account")
    @Test
    void itShouldTestActivate(){

        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumberAndCustomer_Id(account.getAccountNumber(), customer.getId())).thenReturn(Optional.of(account));

        ActivateDTO activateDTO = new ActivateDTO(account.getAccountNumber(), customer.getCustomerId(), "");

        DefaultApiResponse withResponse = accountService.activate(activateDTO);

        Assertions.assertNotNull(withResponse);
        Assertions.assertEquals("00", withResponse.getStatus());
        Assertions.assertEquals(Status.ACTIVE, account.getStatus());

    }
}
