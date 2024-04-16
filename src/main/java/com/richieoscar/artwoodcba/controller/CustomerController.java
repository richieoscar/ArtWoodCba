package com.richieoscar.artwoodcba.controller;

import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.SignUpRequest;
import com.richieoscar.artwoodcba.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<DefaultApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        DefaultApiResponse response = customerService.register(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<DefaultApiResponse> customerInfo(@PathVariable("customerId") String customerId) {
        DefaultApiResponse response = customerService.customerInfo(customerId);
        return ResponseEntity.ok(response);
    }
}
