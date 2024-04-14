package com.richieoscar.artwoodcba.service.impl;

import com.richieoscar.artwoodcba.domain.Customer;
import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.LoginRequestDTO;
import com.richieoscar.artwoodcba.dto.LoginResponseDTO;
import com.richieoscar.artwoodcba.dto.ResetPasswordDTO;
import com.richieoscar.artwoodcba.exception.AuthException;
import com.richieoscar.artwoodcba.repository.CustomerRepository;
import com.richieoscar.artwoodcba.service.AuthService;
import com.richieoscar.artwoodcba.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerRepository customerRepository;

    @Override
    public DefaultApiResponse login(LoginRequestDTO loginRequestDTO) {
        DefaultApiResponse defaultApiResponse = new DefaultApiResponse();
        defaultApiResponse.setStatus("99");
        defaultApiResponse.setMessage("Unable to authenticate at this time");
        String accessToken = null;
        String refreshToken = null;
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password());
        try {
            log.info("Authenticating User");
            authenticationManager.authenticate(userToken);
            log.info("Authentication Successful");
            log.info("Generating Token");
            Optional<Customer> customer = customerRepository.findByEmail(loginRequestDTO.email());
            accessToken = jwtService.generateToken(customer.get());
            refreshToken = jwtService.generateRefreshToken(customer.get());
            log.info("Token Generated");
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(accessToken, refreshToken);
            defaultApiResponse.setStatus("00");
            defaultApiResponse.setMessage("Login Successful");
            defaultApiResponse.setData(loginResponseDTO);

        } catch (Exception e) {
            log.error("Error Occurred while authenticating {}", e.getMessage());
            throw new AuthException("Invalid Credentials");
        }

        return defaultApiResponse;
    }

    @Override
    public ResetPasswordDTO resetPassword(ResetPasswordDTO resetPasswordDTO) {
        return null;
    }
}
