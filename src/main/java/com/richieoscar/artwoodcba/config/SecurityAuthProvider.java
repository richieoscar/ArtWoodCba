package com.richieoscar.artwoodcba.config;

import com.richieoscar.artwoodcba.service.CustomerDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@RequiredArgsConstructor
public class SecurityAuthProvider {
    private final CustomerDetailsService customerDetailsService;
    private final SecurityPasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder.passwordEncoder());
        authenticationProvider.setUserDetailsService(customerDetailsService);
        return authenticationProvider;
    }
}

