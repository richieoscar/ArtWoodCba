package com.richieoscar.artwoodcba.controller;

import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.LoginRequestDTO;
import com.richieoscar.artwoodcba.dto.SignUpRequest;
import com.richieoscar.artwoodcba.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<DefaultApiResponse> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
            log.info("AuthenticationController::login");
            DefaultApiResponse loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        }
}
