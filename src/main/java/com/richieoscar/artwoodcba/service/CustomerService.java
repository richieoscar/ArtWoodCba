package com.richieoscar.artwoodcba.service;

import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.SignUpRequest;

public interface CustomerService {

    DefaultApiResponse register(SignUpRequest signUpRequest);

    DefaultApiResponse customerInfo(String customerId);
}
