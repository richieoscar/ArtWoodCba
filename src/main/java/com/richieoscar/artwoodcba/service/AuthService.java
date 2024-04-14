package com.richieoscar.artwoodcba.service;

import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import com.richieoscar.artwoodcba.dto.LoginRequestDTO;
import com.richieoscar.artwoodcba.dto.LoginResponseDTO;
import com.richieoscar.artwoodcba.dto.ResetPasswordDTO;

public interface AuthService {

    DefaultApiResponse login(LoginRequestDTO loginRequestDTO);
    ResetPasswordDTO resetPassword(ResetPasswordDTO resetPasswordDTO);
}
