package com.richieoscar.artwoodcba.exception;

import com.richieoscar.artwoodcba.dto.DefaultApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AppExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DefaultApiResponse handleInvalidArgument(MethodArgumentNotValidException ex) {
        DefaultApiResponse apiResponse = new DefaultApiResponse();
        Map<String, String> errorMap = new HashMap<>();
        ex
                .getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errorMap.put(error.getField(), error.getDefaultMessage());
                });
        apiResponse.setStatus("400");
        apiResponse.setMessage("Field validation failed");
        apiResponse.setData(errorMap);
        return apiResponse;
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public DefaultApiResponse handlePasswordException(PasswordDoNotMatchException ex) {
        DefaultApiResponse response = new DefaultApiResponse();
        response.setStatus("91");
        response.setMessage(ex.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler
    public DefaultApiResponse handleCustomerException(CustomerException ex) {
        DefaultApiResponse response = new DefaultApiResponse();
        response.setStatus("91");
        response.setMessage(ex.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler
    public DefaultApiResponse handleAccountException(AccountException ex) {
        DefaultApiResponse response = new DefaultApiResponse();
        response.setStatus("92");
        response.setMessage(ex.getMessage());
        return response;
    }
}
