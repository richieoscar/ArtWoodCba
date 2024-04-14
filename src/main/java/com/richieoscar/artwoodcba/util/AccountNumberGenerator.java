package com.richieoscar.artwoodcba.util;

import com.richieoscar.artwoodcba.domain.IdGen;
import com.richieoscar.artwoodcba.exception.IdGenException;
import com.richieoscar.artwoodcba.repository.IdGenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNumberGenerator {
    private final IdGenRepository idGenRepository;

    public  String generateAccountNumber() throws IdGenException{
        return  generateId();
    }

    private String generateId() throws IdGenException {
        String uniqueValue;
        String generatedValue;
        try {
            uniqueValue = String.valueOf(idGenRepository.count());
            String _generatedValue = RandomStringUtils.random(11 - uniqueValue.length(), "0123456789");
            generatedValue = _generatedValue + uniqueValue;
            IdGen refDTO = new IdGen();
            refDTO.setRef( _generatedValue + uniqueValue);
            idGenRepository.save(refDTO);

            return generatedValue;
        } catch (Exception ex) {
            log.info("Couldn't generate transaction reference => {}", ex.getMessage());
            throw new IdGenException("Unexpected error occurred while generating reference");
        }
    }
}
