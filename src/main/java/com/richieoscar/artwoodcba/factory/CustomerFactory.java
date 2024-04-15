package com.richieoscar.artwoodcba.factory;

import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.dto.SignUpRequest;
import com.richieoscar.artwoodcba.dto.enums.Status;
import com.richieoscar.artwoodcba.dto.enums.SystemRole;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
@Component
public class CustomerFactory {
        public  Customer createCustomer(SignUpRequest signUpRequest, String encodePassword) {
            Customer customer = new Customer();
            customer.setEmail(signUpRequest.email());
            customer.setPassword(encodePassword);
            customer.setRole(SystemRole.CUSTOMER);
            customer.setFirstName(signUpRequest.firstName());
            customer.setLastName(signUpRequest.lastName());
            customer.setPhone(signUpRequest.phone());
            customer.setRegistrationDate(LocalDateTime.now());
            customer.setStatus(Status.ACTIVE);
            customer.setCustomerId(UUID.randomUUID().toString());
            return customer;
        }
}
