package com.richieoscar.artwoodcba.service.impl;

import com.richieoscar.artwoodcba.config.SecurityPasswordEncoder;
import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.SignUpRequest;
import com.richieoscar.artwoodcba.dto.enums.SystemRole;
import com.richieoscar.artwoodcba.factory.CustomerFactory;
import com.richieoscar.artwoodcba.repository.AccountRepository;
import com.richieoscar.artwoodcba.repository.CustomerRepository;
import com.richieoscar.artwoodcba.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CustomerServiceImpl.class)
class CustomerServiceImplTest {

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private SecurityPasswordEncoder passwordEncoder;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private CustomerFactory customerFactory;

    @Autowired
    private CustomerServiceImpl customerService;


    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setFirstName("Tolu");
        customer.setPhone("0902345678");
        customer.setLastName("Yolo");
        customer.setRole(SystemRole.CUSTOMER);
        customer.setEmail("tolu@gmail.com");

    }

    @Test
    @DisplayName("test Register Customer")
    void itShouldRegister() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setFirstName("Tolu");
        customer.setPhone("0902345678");
        customer.setLastName("Yolo");
        customer.setRole(SystemRole.CUSTOMER);
        customer.setEmail("boy@yopmail@gmail.com");


        SignUpRequest signUpRequest = new SignUpRequest("Password@34", "Password@34", "boy@yopmail.com",
                "08088998893", "Tolu", "Bakare");

        PasswordEncoder encoder = new SecurityPasswordEncoder().passwordEncoder();
        when(passwordEncoder.passwordEncoder()).thenReturn(encoder);

        when(customerRepository.findByEmail(signUpRequest.email())).thenReturn(Optional.empty());

        String encodePassword = encoder.encode(signUpRequest.password());

        when(customerFactory.createCustomer(signUpRequest, encodePassword)).thenReturn(customer);

        when(customerRepository.save(customer)).thenReturn(customer);

        DefaultApiResponse response = customerService.register(signUpRequest);

        assertNotNull(response);

        assertNotNull(response.getStatus());
        assertEquals("00",response.getStatus());

        verify(customerRepository,times(2)).findByEmail(signUpRequest.email());
    }

    @Test
    @DisplayName("test Throw Exception")
    void itShouldThrowDataIntegrityException() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setFirstName("Tolu");
        customer.setPhone("08088998893");
        customer.setLastName("Yolo");
        customer.setRole(SystemRole.CUSTOMER);
        customer.setEmail("boy@yopmail@gmail.com");


        SignUpRequest signUpRequest = new SignUpRequest("Password@34", "Password@34", "boy@yopmail.com",
                "08088998893", "Tolu", "Bakare");

        when(customerRepository.save(customer)).thenThrow(DataIntegrityViolationException.class);

        doThrow(new DataIntegrityViolationException(""))
                .when(customerRepository).save(customer);


        assertThrows(DataIntegrityViolationException.class, ()-> customerService.register(signUpRequest));
    }
}
