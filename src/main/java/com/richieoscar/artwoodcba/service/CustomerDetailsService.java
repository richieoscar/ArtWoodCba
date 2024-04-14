package com.richieoscar.artwoodcba.service;

import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDetailsService  implements UserDetailsService {

    private final CustomerRepository customerRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> optCustomer = customerRepository.findByEmail(username);
        if (optCustomer.isPresent()) {
            Customer customer = optCustomer.get();
            return new User(customer.getEmail(), customer.getPassword(), List.of(new SimpleGrantedAuthority(customer.getRole().name())));
        }
        return null;
    }
}
