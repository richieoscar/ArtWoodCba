package com.richieoscar.artwoodcba.service.impl;

import com.richieoscar.artwoodcba.config.SecurityPasswordEncoder;
import com.richieoscar.artwoodcba.domain.Account;
import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.RegisterResponseDTO;
import com.richieoscar.artwoodcba.dto.SignUpRequest;
import com.richieoscar.artwoodcba.exception.CustomerException;
import com.richieoscar.artwoodcba.factory.CustomerFactory;
import com.richieoscar.artwoodcba.repository.AccountRepository;
import com.richieoscar.artwoodcba.repository.CustomerRepository;
import com.richieoscar.artwoodcba.service.CustomerService;
import com.richieoscar.artwoodcba.util.PasswordManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final SecurityPasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final CustomerFactory customerFactory;

    @Override
    public DefaultApiResponse register(SignUpRequest signUpRequest) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        defaultApiResponse.setStatus("91");
        defaultApiResponse.setMessage("Unable to Register Customer");
        Optional<Customer> existingCustomer = customerRepository.findByEmail(signUpRequest.email());
        if (existingCustomer.isPresent()) throw new CustomerException("Customer with email exist");
        try {
            PasswordManager.passwordMatch(signUpRequest.password(), signUpRequest.confirmPassword());
            PasswordManager.validatePassword(signUpRequest.password(), signUpRequest.confirmPassword());
            String encodePassword = passwordEncoder.passwordEncoder().encode(signUpRequest.password());
            Customer customer = customerFactory.createCustomer(signUpRequest, encodePassword);
            customer = customerRepository.save(customer);
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Customer Registered Successfully");
            defaultApiResponse.setData(new RegisterResponseDTO(customer.getCustomerId()));
        } catch (DataIntegrityViolationException e) {
            defaultApiResponse.setStatus("91");
            defaultApiResponse.setMessage("Customer with email and phone already exist");
        } catch (Exception e) {
            defaultApiResponse.setStatus("99");
            defaultApiResponse.setMessage(e.getMessage());
        }
        return defaultApiResponse;
    }

    @Override
    public DefaultApiResponse customerInfo(String customerId) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        record CustomerAccount(String accountId, String status){}
        record  CustomerInfo(String firstName, String lastName, String email, String customerId, List<CustomerAccount> accounts){}
        Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
        if (customer.isEmpty()) throw new CustomerException("Customer not found");
        List<Account> accounts = accountRepository.findAllByCustomer_Id(customer.get().getId());
        List<CustomerAccount> accountList = accounts.stream().map(account -> new CustomerAccount(account.getAccountNumber(), account.getStatus().toString())).collect(Collectors.toList());
        CustomerInfo customerInfo = new CustomerInfo(customer.get().getFirstName(), customer.get().getLastName(), customer.get().getEmail(), customerId, accountList);
        defaultApiResponse.setStatus("00");
        defaultApiResponse.setMessage("Customer Information Retrieved Successfully");
        defaultApiResponse.setData(customerInfo);
        return defaultApiResponse;
    }


}
